package com.Mohcasi3.Xurgox.presentation.game

import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.domain.model.BoardCell

enum class GamePhase { IDLE, PLAYING, CELEBRATING, FINISHED }

enum class TimerLevel { CALM, WARN, DANGER }

data class LaneSnapshot(
    val index: Int,
    val vehicles: List<Float>,
    val mirrored: Boolean,
    val safe: Boolean
)

data class GameUiState(
    val stageNameRes: Int = R.string.stage_dawn,
    val phase: GamePhase = GamePhase.IDLE,
    val lanes: List<LaneSnapshot> = emptyList(),
    val badges: List<BoardCell> = emptyList(),
    val perches: List<BoardCell> = emptyList(),
    val roosterLane: Int = 0,
    val roosterColumn: Int = 1,
    val controlsEnabled: Boolean = true,
    val secondsLeft: Int = 60,
    val timerLevel: TimerLevel = TimerLevel.CALM,
    val score: Int = 0,
    val badgeCount: Int = 0,
    val streak: Int = 1,
    val dashCharge: Int = 0,
    val dashReady: Boolean = false,
    val crossingIndex: Int = 1,
    val rushTier: Int = 1,
    val showHint: Boolean = true,
    val celebrate: Boolean = false,
    val flash: Boolean = false,
    val paused: Boolean = false,
    val highContrast: Boolean = false,
    val rhythmHints: Boolean = true,
    val mirrorControls: Boolean = false,
    val gainSeconds: Int = 0
)
