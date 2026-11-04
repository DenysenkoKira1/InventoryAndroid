package com.example.inventory.ui.features

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.inventory.navigation.NavGraphs
import com.example.inventory.ui.features.inventory_review.InventoryReviewScreen
import com.example.inventory.ui.theme.InventoryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventoryTheme {
                NavGraphs(onMessage = { message ->
                    Toast.makeText(
                        this, message, Toast.LENGTH_SHORT
                    ).show()
                })
            }
        }
    }
}