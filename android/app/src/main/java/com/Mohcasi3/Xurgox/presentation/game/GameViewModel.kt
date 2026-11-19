package com.Mohcasi3.Xurgox.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.data.sample.SampleData
import com.Mohcasi3.Xurgox.domain.model.BoardCell
import com.Mohcasi3.Xurgox.domain.model.ControlSide
import com.Mohcasi3.Xurgox.domain.model.CrossingLayout
import com.Mohcasi3.Xurgox.domain.model.GameSettings
import com.Mohcasi3.Xurgox.domain.model.LaneDirection
import com.Mohcasi3.Xurgox.domain.model.RunOutcome
import com.Mohcasi3.Xurgox.domain.model.RunResult
import com.Mohcasi3.Xurgox.domain.model.Stage
import com.Mohcasi3.Xurgox.domain.repository.ProgressRepository
import com.Mohcasi3.Xurgox.domain.repository.SettingsRepository
import com.Mohcasi3.Xurgox.domain.usecase.GenerateCrossingUseCase
import com.Mohcasi3.Xurgox.domain.usecase.HopVerdict
import com.Mohcasi3.Xurgox.domain.usecase.ResolveHopUseCase
import com.Mohcasi3.Xurgox.domain.usecase.ScoreRunUseCase
import com.Mohcasi3.Xurgox.domain.usecase.StepTrafficUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

sealed interface GameEvent {
    data class Finished(val result: RunResult) : GameEvent
}

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val generateCrossing: GenerateCrossingUseCase,
    private val stepTraffic: StepTrafficUseCase,
    private val resolveHop: ResolveHopUseCase,
    private val scoreRun: ScoreRunUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val events = Channel<GameEvent>(Channel.BUFFERED)
    val eventFlow = events.receiveAsFlow()

    private var loopJob: Job? = null
    private var settings: GameSettings = GameSettings(false, true, ControlSide.RIGHT, false)
    private var stage: Stage = SampleData.stages.first()
    private var layout: CrossingLayout? = null

    private var elapsedSec = 0f
    private var timeLeftMs = GameConfig.CROSSING_TIME_MS
    private var earnedPoints = 0
    private var lanesCrossed = 0
    private var badgesTaken = 0
    private var dashesUsed = 0
    private var streak = 1
    private var bestStreak = 1
    private var dashCharge = 0
    private var crossingIndex = 0
    private var rushTier = 1
    private var attempt = 0
    private var collectedBadgeIds = mutableListOf<String>()
    private var remainingBadges = mutableListOf<BoardCell>()
    private var remainingPerches = mutableListOf<BoardCell>()
    private var ownedBadgeIds: Set<String> = emptySet()
    private var collided = false

    private var roosterLane = 0
    private var roosterColumn = GameConfig.COLUMN_COUNT / 2
    private var hopEndSec = 0f
    private var pendingVerdict: HopVerdict? = null
    private var pendingDash = false
    private var lastHopSec = 0f
    private var hintUntilSec = GameConfig.HINT_VISIBLE_MS / 1000f
    private var gainUntilSec = 0f
    private var gainSeconds = 0
    private var started = false
    private var running = false
    private var finished = false
    private var paused = false

    fun onEnter() {
        settings = settingsRepository.settings()
        val selected = progressRepository.progress().selectedStage
        if (!started || finished || selected != stage.index) {
            startNewRun()
        } else {
            paused = false
            publish()
            startLoop()
        }
    }

    fun abandon() {
        finished = true
        running = false
        loopJob?.cancel()
        loopJob = null
        publish()
    }

    fun onLeave() {
        pause()
    }

    fun pause() {
        paused = true
        loopJob?.cancel()
        loopJob = null
        publish()
    }

    fun resume() {
        if (finished) return
        paused = false
        startLoop()
        publish()
    }

    fun restart() {
        startNewRun()
    }

    private fun startNewRun() {
        loopJob?.cancel()
        loopJob = null
        while (events.tryReceive().isSuccess) {
            continue
        }
        val progress = progressRepository.progress()
        stage = SampleData.stageOf(progress.selectedStage)
        ownedBadgeIds = progress.collectedBadgeIds
        collided = false
        attempt += 1
        elapsedSec = 0f
        timeLeftMs = GameConfig.CROSSING_TIME_MS
        earnedPoints = 0
        lanesCrossed = 0
        badgesTaken = 0
        dashesUsed = 0
        streak = 1
        bestStreak = 1
        dashCharge = 0
        crossingIndex = 0
        rushTier = 1
        collectedBadgeIds = mutableListOf()
        roosterLane = 0
        roosterColumn = GameConfig.COLUMN_COUNT / 2
        pendingVerdict = null
        pendingDash = false
        hopEndSec = 0f
        lastHopSec = 0f
        hintUntilSec = GameConfig.HINT_VISIBLE_MS / 1000f
        gainUntilSec = 0f
        gainSeconds = 0
        started = true
        running = false
        finished = false
        paused = false
        buildCrossing()
        publish()
        startLoop()
    }

    private fun buildCrossing() {
        val built = generateCrossing(stage, crossingIndex, attempt, rushTier)
        layout = built
        remainingBadges = built.badges.toMutableList()
        remainingPerches = built.perches.toMutableList()
        roosterLane = 0
        roosterColumn = GameConfig.COLUMN_COUNT / 2
    }

    private fun startLoop() {
        if (loopJob?.isActive == true || finished) return
        loopJob = viewModelScope.launch {
            var last = System.nanoTime()
            while (isActive) {
                delay(GameConfig.TICK_MS)
                val now = System.nanoTime()
                val deltaMs = ((now - last) / 1_000_000L).coerceIn(0L, 96L)
                last = now
                tick(deltaMs)
            }
        }
    }

    private fun tick(deltaMs: Long) {
        if (paused || finished) return
        elapsedSec += deltaMs / 1000f
        if (running) {
            timeLeftMs -= deltaMs
            if (timeLeftMs <= 0L) {
                timeLeftMs = 0L
                finishRun(RunOutcome.TIMEOUT, 0)
                return
            }
        }
        val verdict = pendingVerdict
        if (verdict != null && elapsedSec >= hopEndSec) {
            pendingVerdict = null
            land(verdict)
            if (finished) return
        }
        if (running && streak > 1 && elapsedSec - lastHopSec > GameConfig.STREAK_IDLE_SEC) {
            streak = 1
        }
        publish()
    }

    fun onHop() {
        val board = layout ?: return
        if (finished || paused || pendingVerdict != null) return
        beginRunIfNeeded()
        val target = min(GameConfig.GOAL_LANE, roosterLane + 1)
        if (target == roosterLane) return
        lastHopSec = elapsedSec
        hopEndSec = elapsedSec + GameConfig.HOP_DURATION_MS / 1000f
        pendingDash = false
        roosterLane = target
        pendingVerdict = resolveHop.verdict(board, target, roosterColumn, hopEndSec)
        publish()
    }

    fun onDash() {
        val board = layout ?: return
        if (finished || paused || pendingVerdict != null) return
        if (dashCharge < GameConfig.DASH_CHARGE_COST) return
        beginRunIfNeeded()
        dashCharge = 0
        dashesUsed += 1
        val target = min(GameConfig.GOAL_LANE, roosterLane + GameConfig.DASH_LANES)
        lastHopSec = elapsedSec
        hopEndSec = elapsedSec + GameConfig.DASH_DURATION_MS / 1000f
        pendingDash = true
        roosterLane = target
        pendingVerdict = resolveHop.verdict(board, target, roosterColumn, hopEndSec)
        publish()
    }

    fun onShift(delta: Int) {
        if (finished || paused || pendingVerdict != null) return
        beginRunIfNeeded()
        roosterColumn = (roosterColumn + delta).coerceIn(0, GameConfig.COLUMN_COUNT - 1)
        lastHopSec = elapsedSec
        publish()
    }

    private fun beginRunIfNeeded() {
        if (!running) {
            running = true
            hintUntilSec = elapsedSec + GameConfig.HINT_VISIBLE_MS / 1000f
        }
    }

    private fun land(verdict: HopVerdict) {
        if (verdict == HopVerdict.COLLISION) {
            collided = true
            finishRun(RunOutcome.COLLISION, roosterLane)
            return
        }
        val advanced = if (pendingDash) GameConfig.DASH_LANES else 1
        pendingDash = false
        lanesCrossed += advanced
        earnedPoints += GameConfig.SCORE_PER_LANE * advanced
        addTime(GameConfig.TIME_PER_LANE_MS * advanced)
        if (verdict == HopVerdict.NEAR_MISS && streak < GameConfig.MAX_STREAK) {
            streak += 1
            bestStreak = max(bestStreak, streak)
        }
        collectBadgeAt(roosterLane, roosterColumn)
        bankAt(roosterLane, roosterColumn)
        if (roosterLane >= GameConfig.GOAL_LANE) {
            completeCrossing()
        }
    }

    private fun collectBadgeAt(lane: Int, column: Int) {
        val cell = remainingBadges.firstOrNull { it.lane == lane && it.column == column } ?: return
        remainingBadges.remove(cell)
        badgesTaken += 1
        earnedPoints += scoreRun.badgeValue(streak)
        addTime(GameConfig.TIME_PER_BADGE_MS)
        dashCharge = min(GameConfig.DASH_CHARGE_COST, dashCharge + 1)
        val pool = SampleData.badgesForStage(stage.index)
        val fresh = pool.firstOrNull { !ownedBadgeIds.contains(it.id) && !collectedBadgeIds.contains(it.id) }
        if (fresh != null) collectedBadgeIds.add(fresh.id)
    }

    private fun bankAt(lane: Int, column: Int) {
        if (streak < 2) return
        val cell = remainingPerches.firstOrNull { it.lane == lane && it.column == column } ?: return
        remainingPerches.remove(cell)
        earnedPoints += scoreRun.bankValue(streak)
        addTime(scoreRun.bankTimeMs(streak))
        streak = 1
    }

    private fun addTime(millis: Long) {
        if (millis <= 0L) return
        timeLeftMs += millis
        gainSeconds = (millis / 1000L).toInt()
        gainUntilSec = elapsedSec + GameConfig.GAIN_LABEL_MS / 1000f
    }

    private fun completeCrossing() {
        earnedPoints += GameConfig.SCORE_PER_CROSSING
        crossingIndex += 1
        if (crossingIndex >= GameConfig.CROSSINGS_PER_STAGE) {
            finishRun(RunOutcome.CLEARED, 0)
            return
        }
        rushTier = min(GameConfig.RUSH_TIER_MAX, rushTier + 1)
        viewModelScope.launch {
            delay(GameConfig.CELEBRATION_MS)
            if (finished) return@launch
            buildCrossing()
            publish()
        }
        publish()
    }

    private fun finishRun(outcome: RunOutcome, collisionLane: Int) {
        if (finished) return
        finished = true
        running = false
        loopJob?.cancel()
        loopJob = null
        val secondsLeft = (timeLeftMs / 1000L).toInt().coerceAtLeast(0)
        val score = scoreRun.finalScore(earnedPoints, secondsLeft, !settings.rhythmHints)
        val result = RunResult(
            outcome = outcome,
            stage = stage.index,
            score = score,
            badges = badgesTaken,
            lanes = lanesCrossed,
            crossings = crossingIndex,
            bestStreak = bestStreak,
            secondsLeft = secondsLeft,
            collisionLane = collisionLane,
            dashesUsed = dashesUsed,
            stageCleared = outcome == RunOutcome.CLEARED,
            collectedBadgeIds = collectedBadgeIds.toList()
        )
        publish()
        viewModelScope.launch {
            delay(if (outcome == RunOutcome.COLLISION) GameConfig.FLASH_MS else 420L)
            events.send(GameEvent.Finished(result))
        }
    }

    private fun publish() {
        val board = layout
        val lanes = if (board == null) {
            emptyList()
        } else {
            board.lanes.map { lane ->
                LaneSnapshot(
                    index = lane.index,
                    vehicles = stepTraffic.vehicleCenters(lane, elapsedSec),
                    mirrored = lane.direction == LaneDirection.LEFT,
                    safe = stepTraffic.safeMargin(lane, roosterColumn, elapsedSec) >=
                        GameConfig.SAFE_WINDOW_MS / 1000f
                )
            }
        }
        val secondsLeft = (timeLeftMs / 1000L).toInt().coerceAtLeast(0)
        _state.value = GameUiState(
            stageNameRes = stage.nameRes,
            phase = when {
                finished -> GamePhase.FINISHED
                crossingIndex > 0 && roosterLane >= GameConfig.GOAL_LANE -> GamePhase.CELEBRATING
                running -> GamePhase.PLAYING
                else -> GamePhase.IDLE
            },
            lanes = lanes,
            badges = remainingBadges.toList(),
            perches = remainingPerches.toList(),
            roosterLane = roosterLane,
            roosterColumn = roosterColumn,
            controlsEnabled = !finished && !paused && pendingVerdict == null,
            secondsLeft = secondsLeft,
            timerLevel = when {
                secondsLeft < 10 -> TimerLevel.DANGER
                secondsLeft < 20 -> TimerLevel.WARN
                else -> TimerLevel.CALM
            },
            score = earnedPoints,
            badgeCount = badgesTaken,
            streak = streak,
            dashCharge = dashCharge,
            dashReady = dashCharge >= GameConfig.DASH_CHARGE_COST,
            crossingIndex = min(GameConfig.CROSSINGS_PER_STAGE, crossingIndex + 1),
            rushTier = rushTier,
            showHint = elapsedSec < hintUntilSec,
            celebrate = roosterLane >= GameConfig.GOAL_LANE && !finished,
            flash = finished && collided,
            paused = paused,
            highContrast = settings.highContrast,
            rhythmHints = settings.rhythmHints,
            mirrorControls = settings.controlSide == ControlSide.LEFT,
            gainSeconds = if (elapsedSec < gainUntilSec) gainSeconds else 0
        )
    }

    override fun onCleared() {
        loopJob?.cancel()
        loopJob = null
        events.close()
        super.onCleared()
    }
}
