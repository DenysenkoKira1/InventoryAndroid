package com.example.inventory.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoutes : NavKey {
    @Serializable
    data object Main : NavRoutes

    @Serializable
    data object SearchInventory : NavRoutes
}