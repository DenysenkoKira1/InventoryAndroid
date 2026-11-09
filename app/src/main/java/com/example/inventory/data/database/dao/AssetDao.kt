package com.example.inventory.data.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Update
import com.example.inventory.data.database.entities.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY inventory_number")
    fun observeAll(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getById(id: Long): AssetEntity?

    @Query("SELECT * FROM assets WHERE serial_number = :serial")
    suspend fun findBySerial(serial: String): AssetEntity?

    @Query("SELECT * FROM assets WHERE inventory_number = :number")
    suspend fun findByInventoryNumber(number: String): AssetEntity?

    @Update
    suspend fun update(asset: AssetEntity): Int

    @Query("UPDATE assets SET state = 0 WHERE state = 1")
    suspend fun clearInventoryState(): Int
}