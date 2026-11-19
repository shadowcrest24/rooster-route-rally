package com.Mohcasi3.Xurgox.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.core.di.ServiceLocator
import com.Mohcasi3.Xurgox.core.theme.AppPalette
import com.Mohcasi3.Xurgox.core.ui.AppBackground
import com.Mohcasi3.Xurgox.core.ui.BadgeChip
import com.Mohcasi3.Xurgox.core.ui.CurbBand
import com.Mohcasi3.Xurgox.core.ui.HudPanel
import com.Mohcasi3.Xurgox.core.ui.HudShape
import com.Mohcasi3.Xurgox.core.ui.LaneStrip
import com.Mohcasi3.Xurgox.core.ui.PrimaryButton
import com.Mohcasi3.Xurgox.core.ui.SecondaryButton
import com.Mohcasi3.Xurgox.domain.model.RunResult
import com.Mohcasi3.Xurgox.presentation.dialog.PauseDialog
import kotlin.math.floor

private val BoardFrame = 10.dp

@Composable
fun GameScreen(
    onFinished: (RunResult) -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.onEnter()
        onDispose { viewModel.onLeave() }
    }
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(event.result)
            }
        }
    }

    GameContent(
        state = state,
        onHop = viewModel::onHop,
        onDash = viewModel::onDash,
        onShift = viewModel::onShift,
        onPause = viewModel::pause,
        onResume = viewModel::resume,
        onRestart = viewModel::restart,
        onAbandon = viewModel::abandon,
        onMenu = onMenu
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onHop: () -> Unit,
    onDash: () -> Unit,
    onShift: (Int) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onAbandon: () -> Unit,
    onMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground(
            overlay = listOf(
                AppPalette.BackgroundDeep.copy(alpha = 0.80f),
                AppPalette.SurfaceDeep.copy(alpha = 0.70f),
                AppPalette.BackgroundDeep.copy(alpha = 0.88f)
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 12.dp)
        ) {
            GameHeader(state = state, onPause = onPause)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                LaneBoard(state = state)
                CelebrationBanner(state = state)
                CollisionFlash(state = state)
            }
            GameHint(state = state)
            ControlBar(
                state = state,
                onHop = onHop,
                onDash = onDash,
                onShift = onShift
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (state.paused) {
            PauseDialog(
                onResume = onResume,
                onRestart = onRestart,
                onMenu = {
                    onAbandon()
                    onMenu()
                }
            )
        }
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    val timerColor = when (state.timerLevel) {
        TimerLevel.DANGER -> AppPalette.Magenta
        TimerLevel.WARN -> AppPalette.Amber
        TimerLevel.CALM -> AppPalette.Cyan
    }
    val pulse = rememberInfiniteTransition()
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .padding(top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPause, modifier = Modifier.size(48.dp)) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(28.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.timer_format, state.secondsLeft / 60, state.secondsLeft % 60),
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 30.sp),
                color = timerColor,
                modifier = Modifier
                    .scale(if (state.timerLevel == TimerLevel.DANGER) pulseScale else 1f)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(
                        R.string.crossing_format,
                        state.crossingIndex,
                        GameConfig.CROSSINGS_PER_STAGE
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AppPalette.TextMuted
                )
                Text(
                    text = stringResource(R.string.points_format, state.score),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AppPalette.TextSecondary
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            BadgeChip(
                iconRes = R.drawable.sprite_3,
                text = state.badgeCount.toString(),
                accent = AppPalette.Amber,
                contentDescription = stringResource(R.string.cd_badges)
            )
            Spacer(modifier = Modifier.height(5.dp))
            StreakPips(streak = state.streak)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = stringResource(R.string.rush_format, state.rushTier, GameConfig.RUSH_TIER_MAX),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = AppPalette.TextMuted
            )
        }
    }
}

@Composable
private fun StreakPips(streak: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (index in 1..GameConfig.MAX_STREAK) {
            Spacer(
                modifier = Modifier
                    .size(width = if (index <= streak) 16.dp else 10.dp, height = 5.dp)
                    .background(
                        if (index <= streak) AppPalette.Cyan else AppPalette.Inactive,
                        CircleShape
                    )
            )
        }
    }
}

@Composable
private fun LaneBoard(state: GameUiState) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val availableWidth = if (maxWidth < 380.dp) maxWidth else 380.dp
        val innerWidth = availableWidth - BoardFrame * 2
        val cell = floor(innerWidth.value / GameConfig.COLUMN_COUNT).dp
        val innerHeight = maxHeight - BoardFrame * 2
        val laneHeight = floor(innerHeight.value / GameConfig.ROW_COUNT).dp
        val boardWidth = cell * GameConfig.COLUMN_COUNT + BoardFrame * 2
        val boardHeight = laneHeight * GameConfig.ROW_COUNT + BoardFrame * 2
        val spriteSize = if (cell < laneHeight) cell * 0.78f else laneHeight * 0.78f
        val badgeSize = spriteSize * 0.62f

        val roosterX by animateDpAsState(
            targetValue = cell * state.roosterColumn + (cell - spriteSize) / 2f,
            animationSpec = tween(GameConfig.SHIFT_DURATION_MS.toInt(), easing = FastOutSlowInEasing)
        )
        val roosterY by animateDpAsState(
            targetValue = laneHeight * (GameConfig.GOAL_LANE - state.roosterLane) +
                (laneHeight - spriteSize) / 2f,
            animationSpec = tween(GameConfig.HOP_DURATION_MS.toInt(), easing = FastOutSlowInEasing)
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(boardWidth)
                .height(boardHeight)
                .clip(HudShape)
                .background(AppPalette.BackgroundDeep.copy(alpha = 0.55f), HudShape)
                .border(2.dp, AppPalette.Cyan.copy(alpha = 0.45f), HudShape)
                .padding(BoardFrame)
        ) {
            Column {
                CurbBand(
                    width = cell * GameConfig.COLUMN_COUNT,
                    height = laneHeight,
                    accent = AppPalette.Cyan,
                    label = stringResource(R.string.band_finish)
                )
                for (laneIndex in GameConfig.LANE_COUNT downTo 1) {
                    val snapshot = state.lanes.firstOrNull { it.index == laneIndex }
                    LaneStrip(
                        laneHeight = laneHeight,
                        cell = cell,
                        vehicles = snapshot?.vehicles.orEmpty(),
                        mirrored = snapshot?.mirrored ?: false,
                        safe = snapshot?.safe ?: true,
                        showRhythm = state.rhythmHints,
                        highContrast = state.highContrast,
                        stateLabel = stringResource(
                            if (snapshot?.safe != false) R.string.lane_clear else R.string.lane_blocked,
                            laneIndex
                        )
                    )
                }
                CurbBand(
                    width = cell * GameConfig.COLUMN_COUNT,
                    height = laneHeight,
                    accent = AppPalette.Magenta,
                    label = stringResource(R.string.band_roost)
                )
            }
            Box(modifier = Modifier.matchParentSize()) {
                state.perches.forEach { cellRef ->
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .offset(
                                x = cell * cellRef.column + (cell - spriteSize * 0.74f) / 2f,
                                y = laneHeight * (GameConfig.GOAL_LANE - cellRef.lane) +
                                    (laneHeight - spriteSize * 0.74f) / 2f
                            )
                            .size(spriteSize * 0.74f)
                    )
                }
                state.badges.forEach { cellRef ->
                    Image(
                        painter = painterResource(R.drawable.sprite_3),
                        contentDescription = stringResource(R.string.cd_route_badge),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .offset(
                                x = cell * cellRef.column + (cell - badgeSize) / 2f,
                                y = laneHeight * (GameConfig.GOAL_LANE - cellRef.lane) +
                                    (laneHeight - badgeSize) / 2f
                            )
                            .size(badgeSize)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_rooster),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .offset(x = roosterX, y = roosterY)
                        .size(spriteSize)
                )
                DashPips(
                    charge = state.dashCharge,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = laneHeight + 4.dp, end = 4.dp)
                )
                GainLabel(
                    seconds = state.gainSeconds,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = laneHeight * 2)
                )
            }
        }
    }
}

@Composable
private fun DashPips(charge: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        for (index in 1..GameConfig.DASH_CHARGE_COST) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(16.dp)
                    .alpha(if (index <= charge) 1f else 0.25f)
            )
        }
    }
}

@Composable
private fun GainLabel(seconds: Int, modifier: Modifier = Modifier) {
    if (seconds <= 0) return
    Text(
        text = stringResource(R.string.gain_seconds_format, seconds),
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
        color = AppPalette.Amber,
        modifier = modifier
    )
}

@Composable
private fun CelebrationBanner(state: GameUiState) {
    if (!state.celebrate) return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        HudPanel(accent = AppPalette.Cyan, fill = AppPalette.SurfaceDeep.copy(alpha = 0.94f)) {
            Text(
                text = stringResource(
                    R.string.crossing_cleared_format,
                    state.crossingIndex,
                    GameConfig.CROSSINGS_PER_STAGE
                ),
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                color = AppPalette.Cyan,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CollisionFlash(state: GameUiState) {
    if (!state.flash) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPalette.Magenta.copy(alpha = 0.35f))
    )
}

@Composable
private fun GameHint(state: GameUiState) {
    if (!state.showHint) {
        Spacer(modifier = Modifier.height(22.dp))
        return
    }
    Text(
        text = stringResource(R.string.game_hint),
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
        color = AppPalette.TextSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

@Composable
private fun ControlBar(
    state: GameUiState,
    onHop: () -> Unit,
    onDash: () -> Unit,
    onShift: (Int) -> Unit
) {
    val sideWidth: Dp = 76.dp
    HudPanel(
        modifier = Modifier.fillMaxWidth(),
        accent = AppPalette.Cyan,
        fill = AppPalette.Surface.copy(alpha = 0.94f),
        contentPadding = 12.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            SecondaryButton(
                text = stringResource(R.string.cta_dash),
                onClick = onDash,
                enabled = state.dashReady && state.controlsEnabled,
                height = 48.dp,
                accent = AppPalette.Amber,
                contentColor = AppPalette.Amber,
                leadingIcon = R.drawable.sprite_5,
                iconIsPhoto = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val leftDelta = if (state.mirrorControls) 1 else -1
                val leftIcon = if (state.mirrorControls) R.drawable.ic_arrow_right else R.drawable.ic_arrow_left
                val leftLabel = if (state.mirrorControls) R.string.cta_right else R.string.cta_left
                SecondaryButton(
                    text = stringResource(leftLabel),
                    onClick = { onShift(leftDelta) },
                    enabled = state.controlsEnabled,
                    height = 64.dp,
                    accent = AppPalette.Cyan,
                    leadingIcon = leftIcon,
                    modifier = Modifier.width(sideWidth)
                )
                PrimaryButton(
                    text = stringResource(R.string.cta_go),
                    onClick = onHop,
                    enabled = state.controlsEnabled,
                    height = 64.dp,
                    gradient = listOf(AppPalette.Magenta, AppPalette.Amber),
                    glow = if (state.dashReady) AppPalette.Cyan else AppPalette.Magenta,
                    textSize = 24.sp,
                    modifier = Modifier.weight(1f)
                )
                val rightDelta = if (state.mirrorControls) -1 else 1
                val rightIcon = if (state.mirrorControls) R.drawable.ic_arrow_left else R.drawable.ic_arrow_right
                val rightLabel = if (state.mirrorControls) R.string.cta_left else R.string.cta_right
                SecondaryButton(
                    text = stringResource(rightLabel),
                    onClick = { onShift(rightDelta) },
                    enabled = state.controlsEnabled,
                    height = 64.dp,
                    accent = AppPalette.Cyan,
                    leadingIcon = rightIcon,
                    modifier = Modifier.width(sideWidth)
                )
            }
        }
    }
}
