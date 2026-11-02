package com.example.inventory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Лише додаткова роль статусу; основна палітра — стандартна ColorScheme.
private val LocalInventoriedColor = compositionLocalOf { InventoriedLight }

val MaterialTheme.inventoriedColor: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalInventoriedColor.current

@Composable
fun InventoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val statusColor = if (darkTheme) InventoriedDark else InventoriedLight

    CompositionLocalProvider(LocalInventoriedColor provides statusColor) {
        MaterialTheme(
            colorScheme = colors,
            typography = InventoryTypography,
            shapes = InventoryShapes,
            content = content
        )
    }
}