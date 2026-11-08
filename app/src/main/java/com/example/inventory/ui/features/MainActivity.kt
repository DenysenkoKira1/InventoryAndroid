package com.example.inventory.ui.features

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.example.inventory.R
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.inventory.navigation.NavGraphs
import com.example.inventory.ui.theme.InventoryTheme
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Попереднє завантаження модуля сканера
        val moduleInstall = ModuleInstall.getClient(this)
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(this))
            .build()

        moduleInstall
            .installModules(moduleInstallRequest)
            .addOnSuccessListener { response ->
                if (response.areModulesAlreadyInstalled()) {
                    Log.d(
                        "BarcodeScanner",
                        application.getString(
                            R.string.module_barcode_already_installed
                        )
                    )
                } else {
                    Log.d(
                        "BarcodeScanner",
                        application.getString(
                            R.string.module_barcode_install_requested
                        )
                    )
                }
            }
            .addOnFailureListener { error ->
                Log.e(
                    "BarcodeScanner",
                    application.getString(
                        R.string.module_barcode_handle_failure
                    ),
                    error
                )
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val alreadyInstalled =
                        task.result.areModulesAlreadyInstalled()
                    Log.d(
                        "BarcodeScanner",
                        application.getString(
                            R.string.module_barcode_complete,
                            alreadyInstalled
                        )
                    )
                } else {
                    Log.d(
                        "BarcodeScanner",
                        application.getString(
                            R.string.module_barcode_request_failed
                        )
                    )
                }
            }

        setContent {
            InventoryTheme {
                NavGraphs()
            }
        }
    }
} 