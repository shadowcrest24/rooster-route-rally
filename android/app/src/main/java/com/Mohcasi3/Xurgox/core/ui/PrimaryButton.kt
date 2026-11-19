package com.Mohcasi3.Xurgox.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Mohcasi3.Xurgox.core.theme.AppPalette

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 58.dp,
    gradient: List<Color> = listOf(AppPalette.Magenta, AppPalette.MagentaDeep),
    glow: Color = AppPalette.Magenta,
    leadingIcon: Int? = null,
    textSize: TextUnit = 18.sp
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessHigh)
    )
    val fill = if (enabled) gradient else listOf(AppPalette.Inactive, AppPalette.SurfaceAlt)
    val outline = if (enabled) glow.copy(alpha = 0.9f) else AppPalette.TextMuted.copy(alpha = 0.5f)
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interaction,
        shape = HudShape,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = AppPalette.TextPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = AppPalette.TextSecondary
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minWidth = 96.dp, minHeight = height)
            .shadow(if (enabled) 16.dp else 0.dp, HudShape, ambientColor = glow, spotColor = glow)
            .background(Brush.horizontalGradient(fill), HudShape)
            .border(1.dp, outline, HudShape)
            .alpha(if (enabled) 1f else 0.45f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = AppPalette.TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = textSize,
                    letterSpacing = 1.4.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
