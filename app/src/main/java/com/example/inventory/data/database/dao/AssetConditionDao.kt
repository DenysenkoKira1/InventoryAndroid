package com.example.inventory.data.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.example.inventory.data.database.entities.AssetConditionEntity

@Dao
internal interface AssetConditionDao {
    @Query("SELECT * FROM asset_conditions ORDER BY code")
    suspend fun getAll(): List<AssetConditionEntity>
}