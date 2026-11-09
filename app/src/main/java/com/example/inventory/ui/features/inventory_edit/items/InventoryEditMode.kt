package com.example.inventory.ui.features.inventory_edit.items

import kotlinx.serialization.Serializable

@Serializable
enum class InventoryEditMode {
    LIMITED,
    FULL
}