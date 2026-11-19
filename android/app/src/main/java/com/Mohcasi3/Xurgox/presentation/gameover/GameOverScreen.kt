package com.Mohcasi3.Xurgox.presentation.gameover

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.core.di.ServiceLocator
import com.Mohcasi3.Xurgox.core.theme.AppPalette
import com.Mohcasi3.Xurgox.core.ui.AppBackground
import com.Mohcasi3.Xurgox.core.ui.EmptyStateText
import com.Mohcasi3.Xurgox.core.ui.HudPanel
import com.Mohcasi3.Xurgox.core.ui.PrimaryButton
import com.Mohcasi3.Xurgox.core.ui.ProgressBarThin
import com.Mohcasi3.Xurgox.core.ui.SecondaryButton
import com.Mohcasi3.Xurgox.core.ui.StatCardData
import com.Mohcasi3.Xurgox.core.ui.StatCardRow
import com.Mohcasi3.Xurgox.domain.model.RunOutcome
import com.Mohcasi3.Xurgox.domain.model.RunResult

@Composable
fun GameOverScreen(
    result: RunResult,
    onPlayAgain: () -> Unit,
    onLevels: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameOverViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        viewModel.bind(result)
    }

    GameOverContent(
        result = result,
        state = state,
        onPlayAgain = onPlayAgain,
        onLevels = onLevels,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    result: RunResult,
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onLevels: () -> Unit,
    onMenu: () -> Unit
) {
    val accent = if (result.outcome == RunOutcome.CLEARED) AppPalette.Cyan else AppPalette.Magenta
    val animatedScore by animateIntAsState(
        targetValue = if (state.bound) result.score else 0,
        animationSpec = tween(700)
    )
    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground(
            overlay = listOf(
                AppPalette.BackgroundDeep.copy(alpha = 0.88f),
                accent.copy(alpha = 0.18f),
                AppPalette.BackgroundDeep.copy(alpha = 0.92f)
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = when (result.outcome) {
                    RunOutcome.CLEARED -> stringResource(R.string.outcome_cleared)
                    RunOutcome.TIMEOUT -> stringResource(R.string.outcome_timeout)
                    RunOutcome.COLLISION -> stringResource(R.string.outcome_collision, result.collisionLane)
                },
                style = MaterialTheme.typography.headlineMedium,
                color = accent,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            HudPanel(
                modifier = Modifier.fillMaxWidth(),
                accent = accent,
                contentPadding = 14.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    StatCardRow(
                        cards = listOf(
                            StatCardData(
                                value = animatedScore.toString(),
                                label = stringResource(R.string.label_score),
                                accent = AppPalette.Cyan
                            ),
                            StatCardData(
                                value = result.lanes.toString(),
                                label = stringResource(R.string.label_lanes),
                                accent = AppPalette.Amber
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StatCardRow(
                        cards = listOf(
                            StatCardData(
                                value = if (result.badges > 0) result.badges.toString() else "",
                                label = stringResource(R.string.label_badges),
                                accent = AppPalette.Amber
                            ),
                            StatCardData(
                                value = if (result.bestStreak > 1) result.bestStreak.toString() else "",
                                label = stringResource(R.string.label_streak),
                                accent = AppPalette.Magenta
                            ),
                            StatCardData(
                                value = if (state.routePointsEarned > 0) state.routePointsEarned.toString() else "",
                                label = stringResource(R.string.label_route_points),
                                accent = AppPalette.Cyan
                            )
                        )
                    )
                    if (state.newBest) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.label_new_best, state.bestScore),
                            style = MaterialTheme.typography.titleMedium,
                            color = AppPalette.Amber,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            MissionSummary(state = state)
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.cta_play_again),
                onClick = onPlayAgain,
                height = 60.dp,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.stageUnlocked) {
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = stringResource(R.string.cta_levels),
                    onClick = onLevels,
                    height = 52.dp,
                    accent = AppPalette.Cyan,
                    leadingIcon = R.drawable.ic_route,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onMenu,
                height = 52.dp,
                accent = AppPalette.TextMuted,
                leadingIcon = R.drawable.icon_home,
                iconIsPhoto = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MissionSummary(state: GameOverUiState) {
    if (state.missionLines.isEmpty()) {
        EmptyStateText(message = stringResource(R.string.mission_none_this_run))
        return
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.missionLines.forEach { line ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.mission_prefix_format, stringResource(line.titleRes)),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = AppPalette.TextSecondary
                    )
                    Text(
                        text = stringResource(R.string.count_of_format, line.current, line.target),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = AppPalette.Cyan
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                ProgressBarThin(
                    progress = if (line.target == 0) 0f else line.current.toFloat() / line.target,
                    accent = AppPalette.Cyan
                )
            }
        }
    }
}
