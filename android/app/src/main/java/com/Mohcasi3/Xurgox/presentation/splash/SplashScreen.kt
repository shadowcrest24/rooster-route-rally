package com.Mohcasi3.Xurgox.presentation.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
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
import com.Mohcasi3.Xurgox.core.ui.CornerBrackets
import com.Mohcasi3.Xurgox.core.ui.HudShape

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val ready by viewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(ready) {
        if (ready) onFinished()
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    val transition = rememberInfiniteTransition()
    val streakOne by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart)
    )
    val streakTwo by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(2900, easing = LinearEasing), RepeatMode.Restart)
    )
    val streakThree by transition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(3400, easing = LinearEasing), RepeatMode.Restart)
    )
    val blob by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AppBackground(
            overlay = listOf(
                AppPalette.BackgroundDeep.copy(alpha = 0.96f),
                AppPalette.SurfaceDeep.copy(alpha = 0.90f),
                AppPalette.BackgroundDeep.copy(alpha = 0.97f)
            )
        )
        LaneStreak(fraction = streakOne, verticalBias = 0.28f)
        LaneStreak(fraction = streakTwo, verticalBias = 0.50f)
        LaneStreak(fraction = streakThree, verticalBias = 0.72f)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-70).dp)
                .size(220.dp)
                .scale(blob)
                .background(AppPalette.Magenta.copy(alpha = 0.12f), CircleShape)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(178.dp)
                    .background(AppPalette.Surface.copy(alpha = 0.55f), HudShape),
                contentAlignment = Alignment.Center
            ) {
                CornerBrackets(accent = AppPalette.Cyan.copy(alpha = 0.6f), length = 20.dp)
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_emblem),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(140.dp)
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.brand_title),
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 38.sp, lineHeight = 42.sp),
                    color = AppPalette.Magenta.copy(alpha = 0.45f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(x = 2.dp, y = 2.dp)
                )
                Text(
                    text = stringResource(R.string.brand_title),
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 38.sp, lineHeight = 42.sp),
                    color = AppPalette.TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.brand_tagline),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, letterSpacing = 1.6.sp),
                color = AppPalette.TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(36.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp),
                color = AppPalette.Cyan,
                trackColor = AppPalette.SurfaceAlt
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.loading_route),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = AppPalette.TextMuted
            )
        }
    }
}

@Composable
private fun LaneStreak(fraction: Float, verticalBias: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val y = size.height * verticalBias
        val length = size.width * 0.30f
        val x = size.width * fraction
        drawLine(
            color = AppPalette.Cyan.copy(alpha = 0.35f),
            start = Offset(x, y),
            end = Offset(x + length, y),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
