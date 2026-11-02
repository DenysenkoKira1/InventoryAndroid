package com.example.inventory.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF087F69),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEFE8),
    onPrimaryContainer = Color(0xFF00382D),

    secondary = Color(0xFF087F69),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCEFE8),
    onSecondaryContainer = Color(0xFF00382D),

    tertiary = Color(0xFF087F69),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDCEFE8),
    onTertiaryContainer = Color(0xFF00382D),

    background = Color(0xFFF3F5F4),
    onBackground = Color(0xFF18221F),
    surface = Color.White,
    onSurface = Color(0xFF18221F),
    surfaceVariant = Color(0xFFDCEFE8),
    onSurfaceVariant = Color(0xFF59665F),
    surfaceTint = Color(0xFF087F69),

    surfaceDim = Color(0xFFD7DDD9),
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F5F4),
    surfaceContainer = Color(0xFFECF0ED),
    surfaceContainerHigh = Color(0xFFE5EBE7),
    surfaceContainerHighest = Color(0xFFDCEFE8),

    outline = Color(0xFF75847D),
    outlineVariant = Color(0xFFBBCAC2),

    inverseSurface = Color(0xFF18221F),
    inverseOnSurface = Color(0xFFE5EFE9),
    inversePrimary = Color(0xFF78D7BD),

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFFCE5E2),
    onErrorContainer = Color(0xFF601410),
    scrim = Color.Black
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF78D7BD),
    onPrimary = Color(0xFF00382D),
    primaryContainer = Color(0xFF244A3D),
    onPrimaryContainer = Color(0xFFE5EFE9),

    secondary = Color(0xFF78D7BD),
    onSecondary = Color(0xFF00382D),
    secondaryContainer = Color(0xFF244A3D),
    onSecondaryContainer = Color(0xFFE5EFE9),

    tertiary = Color(0xFF78D7BD),
    onTertiary = Color(0xFF00382D),
    tertiaryContainer = Color(0xFF244A3D),
    onTertiaryContainer = Color(0xFFE5EFE9),

    background = Color(0xFF0C1B17),
    onBackground = Color(0xFFE5EFE9),
    surface = Color(0xFF152C24),
    onSurface = Color(0xFFE5EFE9),
    surfaceVariant = Color(0xFF244A3D),
    onSurfaceVariant = Color(0xFFB4C5BC),
    surfaceTint = Color(0xFF78D7BD),

    surfaceDim = Color(0xFF0C1B17),
    surfaceBright = Color(0xFF30483D),
    surfaceContainerLowest = Color(0xFF08130F),
    surfaceContainerLow = Color(0xFF11231C),
    surfaceContainer = Color(0xFF152C24),
    surfaceContainerHigh = Color(0xFF1D382E),
    surfaceContainerHighest = Color(0xFF244A3D),

    outline = Color(0xFF819A8C),
    outlineVariant = Color(0xFF40594D),

    inverseSurface = Color(0xFFE5EFE9),
    inverseOnSurface = Color(0xFF18221F),
    inversePrimary = Color(0xFF087F69),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF512A27),
    onErrorContainer = Color(0xFFFFDAD5),
    scrim = Color.Black
)

// Окремий статус інвентаризації
val InventoriedLight = Color(0xFF9C4C00)
val InventoriedDark = Color(0xFFFFB86B)