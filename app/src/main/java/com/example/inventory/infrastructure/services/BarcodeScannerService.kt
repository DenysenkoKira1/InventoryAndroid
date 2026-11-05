package com.example.inventory.infrastructure.services

import android.content.Context
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either
import com.example.inventory.core.functional.toLeft
import com.example.inventory.core.functional.toRight
import com.example.inventory.core.infrastructure.IBarcodeScannerService
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


internal class BarcodeScannerService(
    private val context: Context
): IBarcodeScannerService {
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .enableAutoZoom()
        .build()


    override suspend fun startScan(): Either<Failure, String> {
        val scanner = GmsBarcodeScanning.getClient(context, options)
        return suspendCancellableCoroutine { continuation ->
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    continuation.resume(
                        barcode.rawValue.orEmpty().toRight()
                    )
                }
                .addOnFailureListener { e ->
                    continuation.resume(
                        Failure.BarcodeScannerFailure(
                            e.message.orEmpty()
                        ).toLeft()
                    )
                }
                .addOnCanceledListener {
                    continuation.resume(
                        Failure.BarcodeScannerCancel.toLeft()
                    )

                }

        }

    }
}