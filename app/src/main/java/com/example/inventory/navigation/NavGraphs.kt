package com.example.inventory.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.*
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import com.example.inventory.ui.features.inventory_details.*
import com.example.inventory.ui.features.inventory_edit.*
import com.example.inventory.ui.features.inventory_edit.items.*
import com.example.inventory.ui.features.inventory_info.*
import com.example.inventory.ui.features.main.MainScreen
import com.example.inventory.ui.features.main.MainViewModel
import com.example.inventory.ui.features.search_inventory.*

@Composable
fun NavGraphs() {
    val backStack = rememberNavBackStack(NavRoutes.Main)
    var showCheckedTab by rememberSaveable { mutableStateOf(false) }
    val goBack: () -> Unit = {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }
    val openScreen: (NavKey) -> Unit = { route ->
        if (backStack.lastOrNull() != route) backStack.add(route)
    }

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        onBack = { goBack() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<NavRoutes.Main> {
                val vmMain: MainViewModel = hiltViewModel()
                LaunchedEffect(showCheckedTab) {
                    if (showCheckedTab) {
                        vmMain.onTabSelected(1)
                        showCheckedTab = false
                    }
                }
                MainScreen(
                    vm = vmMain,
                    onNavToSearchInventoryScreen = {
                        openScreen(NavRoutes.SearchInventory)
                    },
                    onNavToInventoryReviewScreen = { assetId ->
                        openScreen(NavRoutes.InventoryReview(assetId))
                    },
                    onNavToInventoryDetailsScreen = { assetId ->
                        openScreen(NavRoutes.InventoryDetails(assetId))
                    }
                )
            }
            entry<NavRoutes.SearchInventory> {
                val vmSearch: SearchInventoryViewModel = hiltViewModel()
                SearchInventoryScreen(
                    vm = vmSearch,
                    onBack = goBack,
                    onNavToInventoryReviewScreen = { assetId ->
                        openScreen(NavRoutes.InventoryReview(assetId))
                    },
                    onNavToInventoryDetailsScreen = { assetId ->
                        openScreen(NavRoutes.InventoryDetails(assetId))
                    }
                )
            }
            entry<NavRoutes.InventoryReview> { key ->
                val vmReview: InventoryReviewViewModel = hiltViewModel()
                InventoryReviewScreen(
                    vm = vmReview,
                    assetId = key.assetId,
                    onNavBack = goBack,
                    onCorrectClick = { id ->
                        openScreen(NavRoutes.InventoryEdit(
                            id, InventoryEditMode.LIMITED
                        ))
                    },
                    onIncorrectClick = { id ->
                        openScreen(NavRoutes.InventoryEdit(
                            id, InventoryEditMode.FULL
                        ))
                    }
                )
            }
            entry<NavRoutes.InventoryDetails> { key ->
                val vmDetails: InventoryDetailsViewModel = hiltViewModel()
                InventoryDetailsScreen(
                    vm = vmDetails,
                    assetId = key.assetId,
                    onNavBack = goBack,
                    onEditClick = { id ->
                        openScreen(NavRoutes.InventoryEdit(
                            id, InventoryEditMode.LIMITED
                        ))
                    }
                )
            }
            entry<NavRoutes.InventoryEdit> { key ->
                val vmEdit: InventoryEditViewModel = hiltViewModel()
                InventoryEditScreen(
                    vm = vmEdit,
                    assetId = key.assetId,
                    mode = key.mode,
                    onNavBack = goBack,
                    onSaved = {
                        if (backStack.lastOrNull() == key) {
                            showCheckedTab = true
                            while (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        }
                    }
                )
            }
        }
    )
}