package com.example.inventory.ui.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.inventory.ui.features.inventory_review.InventoryReviewScreen
import com.example.inventory.ui.theme.InventoryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    MainScreen(
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                    SearchInventoryScreen()
                    InventoryReviewScreen()
                }
            }
        }
    }
}