package com.Mohcasi3.Xurgox.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Mohcasi3.Xurgox.core.theme.AppPalette

val HudShape = RoundedCornerShape(6.dp)

@Composable
fun HudPanel(
    modifier: Modifier = Modifier,
    accent: Color = AppPalette.Cyan,
    fill: Color = AppPalette.Surface.copy(alpha = 0.88f),
    bracketLength: Dp = 16.dp,
    contentPadding: Dp = 14.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(HudShape)
            .background(fill)
            .border(1.dp, accent.copy(alpha = 0.28f), HudShape)
    ) {
        CornerBrackets(accent = accent, length = bracketLength)
        Box(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun CornerBrackets(accent: Color, length: Dp, thickness: Dp = 1.5.dp) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val arm = length.toPx()
        val stroke = thickness.toPx()
        val w = size.width
        val h = size.height
        val inset = stroke
        drawLine(accent, Offset(inset, inset), Offset(inset + arm, inset), stroke, StrokeCap.Square)
        drawLine(accent, Offset(inset, inset), Offset(inset, inset + arm), stroke, StrokeCap.Square)
        drawLine(accent, Offset(w - inset, h - inset), Offset(w - inset - arm, h - inset), stroke, StrokeCap.Square)
        drawLine(accent, Offset(w - inset, h - inset), Offset(w - inset, h - inset - arm), stroke, StrokeCap.Square)
    }
}
