package com.example.inventory.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import com.example.inventory.ui.features.main.MainScreen
import com.example.inventory.ui.features.search_inventory.*

@Composable
fun NavGraphs(onMessage: (String) -> Unit) {
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
            rememberSaveableStateHolderNavEntryDecorator()
        ),
        entryProvider = entryProvider {entry<NavRoutes.Main> {
            MainScreen(
                onSearch = {
                    openScreen(NavRoutes.SearchInventory)
                },
                onScan = {
                    onMessage("Сканер підключимо пізніше")
                },
                onClear = {
                    onMessage("Очищення підключимо пізніше")
                }
            )
        }
            entry<NavRoutes.SearchInventory> {
                SearchInventoryScreen(
                    onBack = goBack,
                    onSearch = {
                        onMessage("Пошук Room підключимо пізніше")
                    }
                )
            }
        }
    )
}