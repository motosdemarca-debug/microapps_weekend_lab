package com.example.dayssince.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

// Pasteles suaves
private val Pink = Color(0xFFF6C6D0)
private val Lilac = Color(0xFFD8C7FF)
private val Mint = Color(0xFFCFF5E7)
private val Sky  = Color(0xFFCDEAFF)
private val Cream = Color(0xFFFFF6E5)

private val LightColors = lightColorScheme(
    primary = Color(0xFF7A6FF0),
    onPrimary = Color.White,
    secondary = Color(0xFF4E8D80),
    onSecondary = Color.White,
    tertiary = Color(0xFF6E76A6),
    surface = Cream,
    onSurface = Color(0xFF2A2B2E),
    surfaceVariant = Color.White,
    outline = Color(0x332A2B2E),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB8B2FF),
    onPrimary = Color(0xFF1E1E1E),
    secondary = Color(0xFFA9E6D5),
    onSecondary = Color(0xFF1E1E1E),
    tertiary = Color(0xFFC0C6EF),
    surface = Color(0xFF1E1E22),
    onSurface = Color(0xFFEDEDF0),
    surfaceVariant = Color(0xFF2A2A30),
    outline = Color(0x55FFFFFF),
)

// Esquinas redonditas por defecto
private val CuteShapes = Shapes(
    extraSmall = RoundedCornerShape(16.dp),
    small = RoundedCornerShape(20.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun DaysSinceTheme(
    useDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDark) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        shapes = CuteShapes,
        content = content
    )
}
