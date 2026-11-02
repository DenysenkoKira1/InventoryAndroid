package com.example.inventory.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.inventory.R

val Roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_semi_bold, FontWeight.SemiBold),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

private val DefaultTypography = Typography()

val InventoryTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = Roboto),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = Roboto),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = Roboto),

    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = Roboto),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = Roboto),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = Roboto),

    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = Roboto),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = Roboto),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = Roboto),

    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = Roboto),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = Roboto),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = Roboto),

    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = Roboto),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = Roboto),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = Roboto)
)