package com.example.inventory.core.repository

import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either
import com.example.inventory.domain.models.Asset
import kotlinx.coroutines.flow.Flow

interface IAssetsRepo {
    fun observeAll(): Flow<Either<Failure, List<Asset>>>
    suspend fun getById(id: Long): Either<Failure, Asset?>
    suspend fun findByInventoryNumber(
        inventoryNumber: String
    ): Either<Failure, Asset?>
    suspend fun findBySerial(serial: String): Either<Failure, Asset?>
    suspend fun update(asset: Asset): Either<Failure, Asset>
    suspend fun clearInventoryState(): Either<Failure, Int>
}