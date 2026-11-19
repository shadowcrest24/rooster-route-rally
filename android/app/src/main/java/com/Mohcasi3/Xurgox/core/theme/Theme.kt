package com.Mohcasi3.Xurgox.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RetroNeonScheme = darkColorScheme(
    primary = AppPalette.Magenta,
    onPrimary = AppPalette.TextPrimary,
    primaryContainer = AppPalette.MagentaDeep,
    onPrimaryContainer = AppPalette.TextOnMagenta,
    secondary = AppPalette.Cyan,
    onSecondary = AppPalette.BackgroundDeep,
    secondaryContainer = AppPalette.SurfaceDeep,
    onSecondaryContainer = AppPalette.Cyan,
    tertiary = AppPalette.Amber,
    onTertiary = AppPalette.BackgroundDeep,
    tertiaryContainer = AppPalette.SurfaceAlt,
    onTertiaryContainer = AppPalette.Amber,
    background = AppPalette.BackgroundDeep,
    onBackground = AppPalette.TextPrimary,
    surface = AppPalette.Surface,
    onSurface = AppPalette.TextPrimary,
    surfaceVariant = AppPalette.SurfaceAlt,
    onSurfaceVariant = AppPalette.TextSecondary,
    outline = AppPalette.TextMuted,
    outlineVariant = AppPalette.Inactive,
    error = AppPalette.Magenta,
    onError = AppPalette.TextPrimary
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RetroNeonScheme,
        typography = AppTypography,
        content = content
    )
}
