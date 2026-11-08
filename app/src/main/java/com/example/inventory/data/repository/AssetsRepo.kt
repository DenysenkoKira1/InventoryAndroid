package com.example.inventory.data.repository

import android.database.SQLException
import androidx.room3.withWriteTransaction
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either
import com.example.inventory.core.functional.toLeft
import com.example.inventory.core.repository.IAssetsRepo
import com.example.inventory.data.database.InventoryDatabase
import com.example.inventory.data.database.entities.AssetEntity
import com.example.inventory.data.mappers.to_entity.toEntity
import com.example.inventory.data.mappers.to_model.toModel
import com.example.inventory.domain.models.Asset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AssetsRepo @Inject constructor(
    private val db: InventoryDatabase
) : IAssetsRepo {
    private val dao = db.assetDao()

    override fun observeAll(): Flow<Either<Failure, List<Asset>>> =
        dao.observeAll()
            .map<List<AssetEntity>, Either<Failure, List<Asset>>> { rows ->
                Either.Right(rows.map { it.toModel() })
            }
            .catch { error ->
                if (error !is SQLException) throw error
                emit(Either.Left(Failure.DbError(error.message.orEmpty())))
            }

    override suspend fun getById(id: Long): Either<Failure, Asset?> =
        query { dao.getById(id)?.toModel() }

    override suspend fun findByInventoryNumber(
        inventoryNumber: String
    ): Either<Failure, Asset?> =
        query {
            dao.findByInventoryNumber(inventoryNumber.trim())?.toModel()
        }

    override suspend fun findBySerial(serial: String): Either<Failure, Asset?> =
        query { dao.findBySerial(serial.trim())?.toModel() }

    override suspend fun update(asset: Asset): Either<Failure, Asset> = try {
        db.withWriteTransaction {
            if (dao.getById(asset.id) == null) {
                return@withWriteTransaction Failure.AssetNotFound.toLeft()
            }
            val sameInventory = dao.findByInventoryNumber(asset.invNumber)
            if (sameInventory != null && sameInventory.id != asset.id) {
                return@withWriteTransaction Failure.DuplicateInventoryNumber.toLeft()
            }
            val sameSerial = dao.findBySerial(asset.srnNumber)
            if (sameSerial != null && sameSerial.id != asset.id) {
                return@withWriteTransaction Failure.DuplicateSerialNumber.toLeft()
            }
            check(dao.update(asset.toEntity()) == 1)
            Either.Right(asset)
        }
    } catch (error: SQLException) {
        Either.Left(Failure.DbError(error.message.orEmpty()))
    }

    override suspend fun clearInventoryState(): Either<Failure, Int> =
        query { dao.clearInventoryState() }

    private suspend fun <T> query(block: suspend () -> T): Either<Failure, T> =
        try {
            Either.Right(block())
        } catch (error: SQLException) {
            Either.Left(Failure.DbError(error.message.orEmpty()))
        }

}