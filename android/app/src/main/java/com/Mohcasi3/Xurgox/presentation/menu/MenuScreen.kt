package com.Mohcasi3.Xurgox.presentation.menu

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.Mohcasi3.Xurgox.core.ui.BadgeChip
import com.Mohcasi3.Xurgox.core.ui.CornerBrackets
import com.Mohcasi3.Xurgox.core.ui.HudShape
import com.Mohcasi3.Xurgox.core.ui.SecondaryButton
import com.Mohcasi3.Xurgox.core.ui.StatCardData
import com.Mohcasi3.Xurgox.core.ui.StatCardRow

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onMissions: () -> Unit,
    onRewards: () -> Unit,
    onCollection: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    val viewModel: MenuViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    MenuContent(
        state = state,
        onPlay = onPlay,
        onLevels = onLevels,
        onMissions = onMissions,
        onRewards = onRewards,
        onCollection = onCollection,
        onTutorial = onTutorial,
        onSettings = onSettings
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onMissions: () -> Unit,
    onRewards: () -> Unit,
    onCollection: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground(
            overlay = listOf(
                AppPalette.BackgroundDeep.copy(alpha = 0.72f),
                AppPalette.SurfaceDeep.copy(alpha = 0.55f),
                AppPalette.BackgroundDeep.copy(alpha = 0.86f)
            )
        )
        HairlineGrid()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_emblem),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.brand_short),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    color = AppPalette.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                BadgeChip(
                    iconRes = R.drawable.sprite_3,
                    text = stringResource(R.string.count_of_format, state.badgesCollected, state.totalBadges),
                    accent = AppPalette.Amber,
                    contentDescription = stringResource(R.string.cd_badges)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            StatCardRow(
                cards = listOf(
                    StatCardData(
                        value = if (state.bestScore > 0) state.bestScore.toString() else "",
                        label = stringResource(R.string.label_best),
                        accent = AppPalette.Cyan
                    ),
                    StatCardData(
                        value = if (state.routePoints > 0) state.routePoints.toString() else "",
                        label = stringResource(R.string.label_route_points),
                        accent = AppPalette.Amber
                    )
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.menu_hint),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                color = AppPalette.TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            PlayTile(
                stageNameRes = state.selectedStageNameRes,
                stageIndex = state.selectedStage,
                onPlay = onPlay
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoTile(
                    iconRes = R.drawable.ic_route,
                    label = stringResource(R.string.cta_levels),
                    counter = stringResource(R.string.count_of_format, state.stagesUnlocked, state.stageCount),
                    accent = AppPalette.Cyan,
                    onClick = onLevels,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    iconRes = R.drawable.ic_flag,
                    label = stringResource(R.string.cta_missions),
                    counter = stringResource(R.string.count_active_format, state.activeMissions),
                    accent = AppPalette.Amber,
                    onClick = onMissions,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoTile(
                    iconRes = R.drawable.ic_trophy,
                    label = stringResource(R.string.cta_rewards),
                    counter = stringResource(R.string.count_of_format, state.claimedTiers, state.tierCount),
                    accent = AppPalette.Magenta,
                    onClick = onRewards,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    iconRes = R.drawable.ic_grid,
                    label = stringResource(R.string.cta_collection),
                    counter = stringResource(R.string.count_of_format, state.badgesCollected, state.totalBadges),
                    accent = AppPalette.Cyan,
                    onClick = onCollection,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.cta_tutorial),
                    onClick = onTutorial,
                    height = 52.dp,
                    accent = if (state.tutorialSeen) AppPalette.TextMuted else AppPalette.Cyan,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_settings),
                    onClick = onSettings,
                    height = 52.dp,
                    accent = AppPalette.TextMuted,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun HairlineGrid() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val step = 48.dp.toPx()
        val color = AppPalette.Cyan.copy(alpha = 0.06f)
        var x = 0f
        while (x < size.width) {
            drawLine(color, Offset(x, 0f), Offset(x, size.height), 1f)
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(color, Offset(0f, y), Offset(size.width, y), 1f)
            y += step
        }
    }
}

@Composable
private fun PlayTile(stageNameRes: Int, stageIndex: Int, onPlay: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessHigh)
    )
    Button(
        onClick = onPlay,
        interactionSource = interaction,
        shape = HudShape,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = AppPalette.TextPrimary
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(136.dp)
            .scale(scale)
            .shadow(18.dp, HudShape, ambientColor = AppPalette.Magenta, spotColor = AppPalette.Magenta)
            .background(
                Brush.horizontalGradient(listOf(AppPalette.Magenta, AppPalette.MagentaDeep)),
                HudShape
            )
            .border(1.dp, AppPalette.Magenta.copy(alpha = 0.9f), HudShape)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = stringResource(R.string.cd_rooster),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(74.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.cta_play),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp),
                    color = AppPalette.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.stage_line_format, stageIndex, stringResource(stageNameRes)),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = AppPalette.TextOnMagenta
                )
            }
        }
    }
}

@Composable
private fun BentoTile(
    iconRes: Int,
    label: String,
    counter: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = HudShape,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = AppPalette.TextPrimary
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
        modifier = modifier
            .height(96.dp)
            .background(AppPalette.Surface.copy(alpha = 0.88f), HudShape)
            .border(1.dp, accent.copy(alpha = 0.28f), HudShape)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CornerBrackets(accent = accent, length = 14.dp)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = AppPalette.TextPrimary
                )
                Text(
                    text = counter,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = accent
                )
            }
        }
    }
}
