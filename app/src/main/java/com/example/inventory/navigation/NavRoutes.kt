package com.example.inventory.navigation

import androidx.navigation3.runtime.NavKey
import com.example.inventory.ui.features.inventory_edit.items.*
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoutes : NavKey {
    @Serializable
    data object Main : NavRoutes

    @Serializable
    data object SearchInventory : NavRoutes

    @Serializable
    data class InventoryReview(val assetId: Long) : NavRoutes

    @Serializable
    data class InventoryEdit(
        val assetId: Long,
        val mode: InventoryEditMode
    ) : NavRoutes
}