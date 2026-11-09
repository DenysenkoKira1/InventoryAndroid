package com.example.inventory.ui.features.inventory_edit.items

import android.net.Uri
import com.example.inventory.domain.models.Asset
import com.example.inventory.domain.models.AssetCondition

data class InventoryEditState(
    val asset: Asset? = null,
    val mode: InventoryEditMode = InventoryEditMode.LIMITED,
    val name: String = "",
    val inventoryNumber: String = "",
    val serialNumber: String = "",
    val count: String = "",
    val comment: String = "",
    val inventoryStates: List<AssetCondition> = emptyList(),
    val savedConditionCode: String = "",
    val selectedConditionCode: String = "",
    val isBottomSheetOpened: Boolean = false,
    val photoFileName: String? = null,
    val photoUri: Uri? = null,
    val isPhotoSourceDialogVisible: Boolean = false,
    val cameraUri: Uri? = null,
    val isCameraLaunchRequested: Boolean = false,
    val isMediaOperationRunning: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    )