package com.example.inventory.core.infrastructure



import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either



interface IBarcodeScannerService {
    suspend fun startScan(): Either<Failure, String>
}