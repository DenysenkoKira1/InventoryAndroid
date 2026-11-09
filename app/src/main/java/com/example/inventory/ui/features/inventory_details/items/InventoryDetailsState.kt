package com.example.inventory.ui.features.inventory_details.items

import android.net.Uri
import com.example.inventory.domain.models.Asset

data class InventoryDetailsState(
    val asset: Asset? = null,
    val conditionName: String = "",
    val photoUri: Uri? = null,
    )