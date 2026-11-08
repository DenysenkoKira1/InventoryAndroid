package com.example.inventory.ui.features.search_inventory.items

import com.example.inventory.core.constants.MIN_SYMBOL_TO_SEARCH
import com.example.inventory.domain.models.Asset

data class SearchInventoryState(
    val searchText: String = "",
    val searchScreenState: SearchScreenState =
        SearchScreenState.ENTER_SERIAL_NUMBER,
    val asset: Asset = Asset(),
    val isGoToInventoryReviewScreen: Boolean = false,
    val isOperationRunning: Boolean = false,
    ) {
    val canSearch: Boolean
        get() = searchText.trim().length >= MIN_SYMBOL_TO_SEARCH
                && !isOperationRunning
}