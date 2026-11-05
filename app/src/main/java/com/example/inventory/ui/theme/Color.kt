package com.example.inventory.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF087F69),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F1E6),
    onPrimaryContainer = Color(0xFF00382D),

    secondary = Color(0xFF52665D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5E8DC),
    onSecondaryContainer = Color(0xFF10251C),

    tertiary = Color(0xFF72556F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFDD7F6),
    onTertiaryContainer = Color(0xFF2B132A),

    background = Color(0xFFF5F7F5),
    onBackground = Color(0xFF1A211D),
    surface = Color.White,
    onSurface = Color(0xFF1A211D),
    surfaceVariant = Color(0xFFE1E6E2),
    onSurfaceVariant = Color(0xFF58635C),
    surfaceTint = Color(0xFF087F69),

    surfaceDim = Color(0xFFD9DFDA),
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF0F3F0),
    surfaceContainer = Color(0xFFEBEFEB),
    surfaceContainerHigh = Color(0xFFE6EBE6),
    surfaceContainerHighest = Color(0xFFE1E6E2),

    outline = Color(0xFF748078),
    outlineVariant = Color(0xFFC4CEC6),

    inverseSurface = Color(0xFF2E3630),
    inverseOnSurface = Color(0xFFEDF2ED),
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
    primaryContainer = Color(0xFF005140),
    onPrimaryContainer = Color(0xFFD4F1E6),

    secondary = Color(0xFFB8CCC0),
    onSecondary = Color(0xFF24392E),
    secondaryContainer = Color(0xFF3A5044),
    onSecondaryContainer = Color(0xFFD5E8DC),

    tertiary = Color(0xFFDEBCDA),
    onTertiary = Color(0xFF41273F),
    tertiaryContainer = Color(0xFF593D56),
    onTertiaryContainer = Color(0xFFFDD7F6),

    background = Color(0xFF111512),
    onBackground = Color(0xFFE0E7E2),
    surface = Color(0xFF191E1A),
    onSurface = Color(0xFFE0E7E2),
    surfaceVariant = Color(0xFF303B34),
    onSurfaceVariant = Color(0xFFB8C3BA),
    surfaceTint = Color(0xFF78D7BD),

    surfaceDim = Color(0xFF111512),
    surfaceBright = Color(0xFF353D37),
    surfaceContainerLowest = Color(0xFF0C100D),
    surfaceContainerLow = Color(0xFF171C18),
    surfaceContainer = Color(0xFF1D231E),
    surfaceContainerHigh = Color(0xFF272F29),
    surfaceContainerHighest = Color(0xFF303B34),

    outline = Color(0xFF89968C),
    outlineVariant = Color(0xFF414D44),

    inverseSurface = Color(0xFFE0E7E2),
    inverseOnSurface = Color(0xFF29312B),
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