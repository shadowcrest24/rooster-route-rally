package com.Mohcasi3.Xurgox.presentation.levels

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.core.di.ServiceLocator
import com.Mohcasi3.Xurgox.core.theme.AppPalette
import com.Mohcasi3.Xurgox.core.ui.HudPanel
import com.Mohcasi3.Xurgox.core.ui.PrimaryButton
import com.Mohcasi3.Xurgox.core.ui.ScreenFrame

@Composable
fun LevelsScreen(
    onMenu: () -> Unit,
    onPlayStage: () -> Unit
) {
    val viewModel: LevelsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    LevelsContent(
        state = state,
        onSelect = viewModel::select,
        onMenu = onMenu,
        onPlayStage = onPlayStage
    )
}

@Composable
private fun LevelsContent(
    state: LevelsUiState,
    onSelect: (Int) -> Unit,
    onMenu: () -> Unit,
    onPlayStage: () -> Unit
) {
    val transition = rememberInfiniteTransition()
    val pulse by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse)
    )
    ScreenFrame(
        title = stringResource(R.string.title_levels),
        subtitle = stringResource(R.string.subtitle_levels),
        onMenu = onMenu
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            state.nodes.forEach { node ->
                StageRow(
                    node = node,
                    selected = node.index == state.selectedStage,
                    pulse = pulse,
                    onSelect = { onSelect(node.index) }
                )
            }
        }
        if (state.lockedNoticeStage > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.stage_locked_notice, state.lockedNoticeStage - 1),
                style = MaterialTheme.typography.labelSmall,
                color = AppPalette.Magenta,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = stringResource(R.string.cta_play_stage, state.selectedStage),
            onClick = onPlayStage,
            height = 58.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StageRow(
    node: StageNode,
    selected: Boolean,
    pulse: Float,
    onSelect: () -> Unit
) {
    val accent = when (node.state) {
        StageState.CLEARED -> AppPalette.Cyan
        StageState.OPEN -> AppPalette.Amber
        StageState.LOCKED -> AppPalette.Inactive
    }
    val borderAlpha = if (node.state == StageState.OPEN) pulse else 0.5f
    val stateLabel = when (node.state) {
        StageState.CLEARED -> stringResource(R.string.state_cleared)
        StageState.OPEN -> stringResource(R.string.state_open)
        StageState.LOCKED -> stringResource(R.string.state_locked)
    }
    HudPanel(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp)
            .border(
                if (selected) 2.dp else 1.dp,
                accent.copy(alpha = borderAlpha),
                com.Mohcasi3.Xurgox.core.ui.HudShape
            )
            .clickable(onClick = onSelect)
            .semantics { stateDescription = stateLabel },
        accent = accent,
        contentPadding = 12.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(accent.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = node.index.toString(),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 22.sp),
                    color = accent
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(node.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = AppPalette.TextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = stringResource(
                        R.string.stage_stats_format,
                        node.bestScore,
                        node.badgesCollected,
                        node.badgeTotal
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AppPalette.TextMuted
                )
            }
            Icon(
                painter = painterResource(
                    when (node.state) {
                        StageState.CLEARED -> R.drawable.ic_check
                        StageState.OPEN -> R.drawable.ic_route
                        StageState.LOCKED -> R.drawable.ic_lock
                    }
                ),
                contentDescription = stateLabel,
                tint = accent,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(26.dp)
            )
        }
    }
}
