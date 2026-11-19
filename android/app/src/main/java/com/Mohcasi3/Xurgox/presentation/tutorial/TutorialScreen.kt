package com.Mohcasi3.Xurgox.presentation.tutorial

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
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
import com.Mohcasi3.Xurgox.core.ui.SecondaryButton

private data class TutorialStep(
    val titleRes: Int,
    val bodyRes: Int,
    val spriteRes: Int,
    val accentIsCyan: Boolean
)

private val steps = listOf(
    TutorialStep(R.string.tutorial_1_title, R.string.tutorial_1_body, R.drawable.sprite_2, true),
    TutorialStep(R.string.tutorial_2_title, R.string.tutorial_2_body, R.drawable.sprite_1, false),
    TutorialStep(R.string.tutorial_3_title, R.string.tutorial_3_body, R.drawable.sprite_3, true),
    TutorialStep(R.string.tutorial_4_title, R.string.tutorial_4_body, R.drawable.sprite_4, false)
)

@Composable
fun TutorialScreen(onMenu: () -> Unit) {
    val viewModel: TutorialViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    TutorialContent(
        state = state,
        onNext = viewModel::next,
        onBack = viewModel::previous,
        onMenu = onMenu
    )
}

@Composable
private fun TutorialContent(
    state: TutorialUiState,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onMenu: () -> Unit
) {
    val index = state.step.coerceIn(0, steps.size - 1)
    val step = steps[index]
    val accent = if (step.accentIsCyan) AppPalette.Cyan else AppPalette.Amber
    val transition = rememberInfiniteTransition()
    val glow by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse)
    )

    ScreenFrame(
        title = stringResource(R.string.title_tutorial),
        subtitle = stringResource(R.string.count_of_format, index + 1, steps.size),
        onMenu = onMenu,
        accent = accent
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        HudPanel(modifier = Modifier.fillMaxWidth(), accent = accent, contentPadding = 18.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .background(accent.copy(alpha = 0.10f * glow), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(step.spriteRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(96.dp)
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(step.titleRes),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    color = accent,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(step.bodyRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppPalette.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (dot in steps.indices) {
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = if (dot == index) 22.dp else 8.dp, height = 8.dp)
                        .background(
                            if (dot == index) AppPalette.Cyan else AppPalette.Inactive,
                            CircleShape
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.cta_back),
                onClick = onBack,
                enabled = index > 0,
                height = 52.dp,
                accent = AppPalette.TextMuted,
                modifier = Modifier.weight(1f)
            )
            PrimaryButton(
                text = stringResource(R.string.cta_next),
                onClick = onNext,
                height = 52.dp,
                gradient = listOf(AppPalette.Cyan, AppPalette.SurfaceDeep),
                glow = AppPalette.Cyan,
                modifier = Modifier.weight(1f)
            )
        }
        if (state.completed) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.tutorial_done),
                style = MaterialTheme.typography.labelSmall,
                color = AppPalette.Cyan,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(glow)
            )
        }
    }
}
