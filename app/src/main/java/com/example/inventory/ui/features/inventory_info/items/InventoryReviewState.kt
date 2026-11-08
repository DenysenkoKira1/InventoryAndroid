package com.example.inventory.ui.features.inventory_info.items

import com.example.inventory.domain.models.Asset

data class InventoryReviewState(
    val asset: Asset? = null,
    val conditionName: String = "",
    )