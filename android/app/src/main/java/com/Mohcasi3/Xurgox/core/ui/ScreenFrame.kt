package com.Mohcasi3.Xurgox.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Mohcasi3.Xurgox.R
import com.Mohcasi3.Xurgox.core.theme.AppPalette

@Composable
fun AppBackground(overlay: List<Color>, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
    }
}

@Composable
fun ScreenFrame(
    title: String,
    subtitle: String,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = AppPalette.Cyan,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground(
            overlay = listOf(
                AppPalette.BackgroundDeep.copy(alpha = 0.86f),
                AppPalette.SurfaceDeep.copy(alpha = 0.78f),
                AppPalette.BackgroundDeep.copy(alpha = 0.92f)
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(top = 8.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = AppPalette.TextPrimary
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accent
                    )
                }
                SecondaryButton(
                    text = stringResource(R.string.cta_menu),
                    onClick = onMenu,
                    height = 48.dp,
                    accent = accent,
                    leadingIcon = R.drawable.icon_home,
                    iconIsPhoto = true
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            content()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
