package com.Mohcasi3.Xurgox.presentation.missions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.Mohcasi3.Xurgox.core.ui.EmptyStateText
import com.Mohcasi3.Xurgox.core.ui.HudPanel
import com.Mohcasi3.Xurgox.core.ui.PrimaryButton
import com.Mohcasi3.Xurgox.core.ui.ProgressBarThin
import com.Mohcasi3.Xurgox.core.ui.ScreenFrame

@Composable
fun MissionsScreen(onMenu: () -> Unit) {
    val viewModel: MissionsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    MissionsContent(state = state, onCollect = viewModel::collect, onMenu = onMenu)
}

@Composable
private fun MissionsContent(
    state: MissionsUiState,
    onCollect: (String) -> Unit,
    onMenu: () -> Unit
) {
    ScreenFrame(
        title = stringResource(R.string.title_missions),
        subtitle = stringResource(R.string.route_points_format, state.routePoints),
        onMenu = onMenu,
        accent = AppPalette.Amber
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.active, key = { it.id }) { card ->
                MissionCardView(card = card, done = false, onCollect = { onCollect(card.id) })
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.label_completed),
            style = MaterialTheme.typography.labelSmall,
            color = AppPalette.Cyan,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (state.completed.isEmpty()) {
            EmptyStateText(message = stringResource(R.string.missions_none_done))
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.completed.take(3).forEach { card ->
                    MissionCardView(card = card, done = true, onCollect = { onCollect(card.id) })
                }
            }
        }
    }
}

@Composable
private fun MissionCardView(card: MissionCard, done: Boolean, onCollect: () -> Unit) {
    val accent = when {
        done -> AppPalette.Cyan
        card.ready -> AppPalette.Amber
        else -> AppPalette.TextMuted
    }
    val stateLabel = when {
        done -> stringResource(R.string.state_claimed)
        card.ready -> stringResource(R.string.state_ready)
        else -> stringResource(R.string.state_open)
    }
    HudPanel(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .semantics { stateDescription = stateLabel },
        accent = accent,
        contentPadding = 12.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(card.titleRes),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = AppPalette.TextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = stringResource(card.descriptionRes),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AppPalette.TextMuted
                )
                Spacer(modifier = Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressBarThin(
                        progress = if (card.target == 0) 0f else card.current.toFloat() / card.target,
                        accent = accent,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.count_of_format, card.current, card.target),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = accent
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (!done && card.ready) {
                PrimaryButton(
                    text = stringResource(R.string.cta_collect, card.reward),
                    onClick = onCollect,
                    height = 48.dp,
                    gradient = listOf(AppPalette.Amber, AppPalette.Magenta),
                    glow = AppPalette.Amber,
                    textSize = 13.sp
                )
            } else {
                Text(
                    text = stringResource(R.string.reward_points_format, card.reward),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = AppPalette.Amber
                )
            }
        }
    }
}
