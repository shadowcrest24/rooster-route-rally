package com.Mohcasi3.Xurgox.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Mohcasi3.Xurgox.core.theme.AppPalette

@Composable
fun ProgressBarThin(
    progress: Float,
    accent: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = 5.dp
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(targetValue = clamped, animationSpec = tween(420))
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(AppPalette.Inactive, RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .fillMaxHeight()
                .background(accent, RoundedCornerShape(2.dp))
        )
    }
}
