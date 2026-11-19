package com.Mohcasi3.Xurgox.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.core.config.GameConfig
import com.Mohcasi3.Xurgox.core.theme.AppPalette

@Composable
fun LaneStrip(
    laneHeight: Dp,
    cell: Dp,
    vehicles: List<Float>,
    mirrored: Boolean,
    safe: Boolean,
    showRhythm: Boolean,
    highContrast: Boolean,
    stateLabel: String,
    modifier: Modifier = Modifier
) {
    val fillAlpha = if (highContrast) 0.95f else 0.82f
    val fillColor = if (highContrast) AppPalette.SurfaceAlt.copy(alpha = fillAlpha) else AppPalette.Surface.copy(alpha = fillAlpha)
    val signal = if (safe) AppPalette.Cyan else AppPalette.Magenta
    val barWidth = if (highContrast) 7.dp else 4.dp
    val vehicleWidth = cell * 0.92f
    val vehicleHeight = laneHeight * 0.80f

    Box(
        modifier = modifier
            .width(cell * GameConfig.COLUMN_COUNT)
            .height(laneHeight)
            .clipToBounds()
            .background(fillColor)
            .semantics { stateDescription = stateLabel }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height / 2f
            drawLine(
                color = AppPalette.TextMuted.copy(alpha = 0.4f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Butt,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(6.dp.toPx(), 10.dp.toPx()),
                    0f
                )
            )
        }
        if (showRhythm) {
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(laneHeight)
                    .background(signal.copy(alpha = if (highContrast) 1f else 0.85f))
            )
        }
        vehicles.forEach { center ->
            val left = cell * center - vehicleWidth / 2f
            Image(
                painter = painterResource(R.drawable.sprite_2),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(x = left, y = (laneHeight - vehicleHeight) / 2f)
                    .size(width = vehicleWidth, height = vehicleHeight)
                    .graphicsLayer(scaleX = if (mirrored) -1f else 1f)
            )
        }
    }
}

@Composable
fun CurbBand(
    width: Dp,
    height: Dp,
    accent: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(accent.copy(alpha = 0.18f))
    ) {
        CornerBrackets(accent = accent, length = 14.dp)
        androidx.compose.material3.Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = accent,
            modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
        )
    }
}
