package com.phonepad.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PhonePadLightColorScheme = lightColorScheme(
    primary = Color(0xFF2B2D6E),
    onPrimary = Color.White,
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    tertiary = Color(0xFF0D9488),
    error = Color(0xFFDC2626),
    onError = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1A2E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFF5F5F7),
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Color(0xFF6B7280),
    outlineVariant = Color(0xFFE0E0E0),
)

private val PhonePadDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7B7FD4),
    onPrimary = Color.White,
    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF121218),
    tertiary = Color(0xFF2DD4BF),
    error = Color(0xFFF87171),
    onError = Color(0xFF121218),
    background = Color(0xFF121218),
    onBackground = Color(0xFFE8E8ED),
    surface = Color(0xFF121218),
    onSurface = Color(0xFFE8E8ED),
    surfaceVariant = Color(0xFF1C1C24),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF9CA3AF),
    outlineVariant = Color(0xFF2A2A35),
)

@Composable
fun PhonePadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PhonePadDarkColorScheme else PhonePadLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
