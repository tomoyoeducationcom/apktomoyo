package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val TomoyoLightColorScheme = lightColorScheme(
    primary = BrandCrimson,
    onPrimary = Color.White,
    primaryContainer = BrandCrimsonSoft,
    onPrimaryContainer = BrandCrimsonDark,
    secondary = CoastalTeal,
    onSecondary = Color.White,
    secondaryContainer = CoastalTealSoft,
    onSecondaryContainer = CoastalTeal,
    tertiary = MangoOlive,
    onTertiary = Color.White,
    background = IvoryCanvas,
    onBackground = InkPrimary,
    surface = PurePaper,
    onSurface = InkPrimary,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = InkSecondary,
    outline = HairlineBorder,
    outlineVariant = HairlineBorder,
    error = StatusDanger,
    onError = Color.White,
    errorContainer = StatusDangerBg,
    onErrorContainer = StatusDanger
)

private val TomoyoDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFB7185),
    onPrimary = Color(0xFF4C0519),
    primaryContainer = Color(0xFF6B1026),
    onPrimaryContainer = Color(0xFFFFE4E6),
    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF042F2E),
    secondaryContainer = Color(0xFF134E4A),
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = MangoGold,
    onTertiary = InkPrimary,
    background = DarkCanvas,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkInkMuted,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

val TomoyoShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun TomoyoParentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TomoyoDarkColorScheme else TomoyoLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TomoyoTypography,
        shapes = TomoyoShapes,
        content = content
    )
}
