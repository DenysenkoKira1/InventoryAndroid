package com.example.inventory.ui.features.inventory_edit.items

import android.net.Uri
import com.example.inventory.domain.models.Asset

data class InventoryEditState(
    val asset: Asset? = null,
    val photoFileName: String? = null,
    val photoUri: Uri? = null,
    val isPhotoSourceDialogVisible: Boolean = false,
    val cameraUri: Uri? = null,
    val isCameraLaunchRequested: Boolean = false,
    val isMediaOperationRunning: Boolean = false,
    )