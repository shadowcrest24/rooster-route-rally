package com.Mohcasi3.Xurgox.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Mohcasi3.Xurgox.core.theme.AppPalette

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 50.dp,
    accent: Color = AppPalette.TextMuted,
    contentColor: Color = AppPalette.TextPrimary,
    leadingIcon: Int? = null,
    iconIsPhoto: Boolean = false,
    textSize: TextUnit = 14.sp
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = HudShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = if (enabled) 0.6f else 0.25f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = AppPalette.TextMuted
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        modifier = modifier
            .defaultMinSize(minWidth = 88.dp, minHeight = height)
            .background(AppPalette.SurfaceAlt.copy(alpha = if (enabled) 0.8f else 0.4f), HudShape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                if (iconIsPhoto) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(leadingIcon),
                        contentDescription = null,
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(leadingIcon),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = textSize, letterSpacing = 1.3.sp)
            )
        }
    }
}
