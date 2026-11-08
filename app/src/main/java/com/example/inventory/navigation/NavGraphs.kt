package com.example.inventory.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import com.example.inventory.ui.features.inventory_info.*
import com.example.inventory.ui.features.main.MainScreen
import com.example.inventory.ui.features.main.MainViewModel
import com.example.inventory.ui.features.search_inventory.*

@Composable
fun NavGraphs() {
    val backStack = rememberNavBackStack(NavRoutes.Main)
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
                MainScreen(
                    vm = vmMain,
                    onNavToSearchInventoryScreen = {
                        openScreen(NavRoutes.SearchInventory)
                    },
                    onNavToInventoryReviewScreen = { assetId ->
                        openScreen(NavRoutes.InventoryReview(assetId))
                    }
                )
            }
            entry<NavRoutes.SearchInventory> {
                val vmSearchInventory: SearchInventoryViewModel =
                    hiltViewModel()
                SearchInventoryScreen(
                    vm = vmSearchInventory,
                    onBack = goBack,
                    onNavToInventoryReviewScreen = { assetId ->
                        openScreen(NavRoutes.InventoryReview(assetId))
                    }
                )
            }
            entry<NavRoutes.InventoryReview> {
                InventoryReviewScreen()
            }
        }
    )
}