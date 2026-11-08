package com.example.inventory.ui.features.main.items

import com.example.inventory.domain.models.Asset

data class MainState(
    val uncheckedAssets: List<Asset> = emptyList(),
    val checkedAssets: List<Asset> = emptyList(),
    val selectedTabIndex: Int = 0,
    val selectedAsset: Asset = Asset(),
    val isGoToInventoryReviewScreen: Boolean = false,
    val isOperationRunning: Boolean = false,
    )