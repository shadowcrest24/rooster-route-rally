package com.Mohcasi3.Xurgox.presentation.rewards

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
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
import com.Mohcasi3.Xurgox.core.ui.StatCardData
import com.Mohcasi3.Xurgox.core.ui.StatCardRow

@Composable
fun RewardsScreen(onMenu: () -> Unit) {
    val viewModel: RewardsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    RewardsContent(state = state, onClaim = viewModel::claim, onMenu = onMenu)
}

@Composable
private fun RewardsContent(
    state: RewardsUiState,
    onClaim: (String) -> Unit,
    onMenu: () -> Unit
) {
    ScreenFrame(
        title = stringResource(R.string.title_rewards),
        subtitle = stringResource(R.string.subtitle_rewards),
        onMenu = onMenu,
        accent = AppPalette.Amber
    ) {
        StatCardRow(
            cards = listOf(
                StatCardData(
                    value = state.routePoints.toString(),
                    label = stringResource(R.string.label_route_points),
                    accent = AppPalette.Amber
                ),
                StatCardData(
                    value = stringResource(R.string.count_of_format, state.claimedCount, state.tierCount),
                    label = stringResource(R.string.label_claimed),
                    accent = AppPalette.Cyan
                )
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        ProgressBarThin(progress = state.nextTierProgress, accent = AppPalette.Amber)
        Spacer(modifier = Modifier.height(10.dp))
        if (state.routePoints == 0 && state.claimedCount == 0) {
            EmptyStateText(message = stringResource(R.string.rewards_empty))
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.rows, key = { it.id }) { row ->
                TierCard(row = row, onClaim = { onClaim(row.id) })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.rewards_disclaimer),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = AppPalette.TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TierCard(row: TierRow, onClaim: () -> Unit) {
    val accent = when {
        row.claimed -> AppPalette.Cyan
        row.affordable -> AppPalette.Amber
        else -> AppPalette.TextMuted
    }
    val stateLabel = when {
        row.claimed -> stringResource(R.string.state_claimed)
        row.affordable -> stringResource(R.string.state_ready)
        else -> stringResource(R.string.state_locked)
    }
    HudPanel(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .semantics { stateDescription = stateLabel },
        accent = accent,
        contentPadding = 12.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(accent.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(row.iconRes),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(row.nameRes),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = AppPalette.TextPrimary
                )
                Text(
                    text = stringResource(R.string.route_points_format, row.cost),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = accent
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            when {
                row.claimed -> Text(
                    text = stringResource(R.string.state_claimed),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = AppPalette.Cyan,
                    modifier = Modifier.padding(end = 6.dp)
                )
                row.affordable -> PrimaryButton(
                    text = stringResource(R.string.cta_claim),
                    onClick = onClaim,
                    height = 48.dp,
                    gradient = listOf(AppPalette.Amber, AppPalette.Magenta),
                    glow = AppPalette.Amber,
                    textSize = 14.sp
                )
                else -> Text(
                    text = stringResource(R.string.tier_missing_format, row.missing),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AppPalette.TextMuted,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .width(86.dp)
                        .padding(end = 4.dp)
                )
            }
        }
    }
}
