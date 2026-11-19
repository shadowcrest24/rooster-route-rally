package com.Mohcasi3.Xurgox.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
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
import com.Mohcasi3.Xurgox.core.ui.HudShape
import com.Mohcasi3.Xurgox.core.ui.ScreenFrame
import com.Mohcasi3.Xurgox.core.ui.StatCardData
import com.Mohcasi3.Xurgox.core.ui.StatCardRow

@Composable
fun CollectionScreen(onMenu: () -> Unit) {
    val viewModel: CollectionViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    CollectionContent(state = state, onSelect = viewModel::select, onMenu = onMenu)
}

@Composable
private fun CollectionContent(
    state: CollectionUiState,
    onSelect: (String) -> Unit,
    onMenu: () -> Unit
) {
    val selected = state.tiles.firstOrNull { it.id == state.selectedId }
    ScreenFrame(
        title = stringResource(R.string.title_collection),
        subtitle = stringResource(R.string.subtitle_collection),
        onMenu = onMenu
    ) {
        StatCardRow(
            cards = listOf(
                StatCardData(
                    value = stringResource(R.string.count_of_format, state.collected, state.total),
                    label = stringResource(R.string.label_collected),
                    accent = AppPalette.Amber
                ),
                StatCardData(
                    value = stringResource(R.string.count_of_format, state.stagesTouched, state.stageCount),
                    label = stringResource(R.string.label_stages),
                    accent = AppPalette.Cyan
                )
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (state.collected == 0) {
            EmptyStateText(message = stringResource(R.string.collection_empty))
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.tiles, key = { it.id }) { tile ->
                BadgeCell(tile = tile, onSelect = { onSelect(tile.id) })
            }
        }
        if (selected != null) {
            Spacer(modifier = Modifier.height(10.dp))
            HudPanel(
                modifier = Modifier.fillMaxWidth(),
                accent = if (selected.collected) AppPalette.Amber else AppPalette.TextMuted,
                contentPadding = 12.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (selected.collected) {
                            stringResource(selected.nameRes)
                        } else {
                            stringResource(R.string.badge_unknown)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = AppPalette.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.badge_source_format,
                            selected.stage,
                            selected.lane
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = AppPalette.TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeCell(tile: BadgeTile, onSelect: () -> Unit) {
    val accent = if (tile.collected) AppPalette.Amber else AppPalette.Inactive
    val stateLabel = if (tile.collected) {
        stringResource(R.string.state_collected)
    } else {
        stringResource(R.string.state_locked)
    }
    Column(
        modifier = Modifier
            .height(96.dp)
            .background(AppPalette.Surface.copy(alpha = 0.82f), HudShape)
            .border(1.dp, accent.copy(alpha = 0.45f), HudShape)
            .clickable(onClick = onSelect)
            .semantics { stateDescription = stateLabel }
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(44.dp)
                    .alpha(if (tile.collected) 1f else 0.18f)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = if (tile.collected) {
                stringResource(tile.nameRes)
            } else {
                stringResource(R.string.badge_hidden_format, tile.stage)
            },
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, lineHeight = 11.sp),
            color = if (tile.collected) AppPalette.TextSecondary else AppPalette.TextMuted,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
