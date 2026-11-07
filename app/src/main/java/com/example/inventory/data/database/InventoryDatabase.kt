package com.example.inventory.data.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.inventory.data.database.dao.AssetConditionDao
import com.example.inventory.data.database.dao.AssetDao
import com.example.inventory.data.database.entities.AssetConditionEntity
import com.example.inventory.data.database.entities.AssetEntity

@Database(
    entities = [AssetEntity::class, AssetConditionEntity::class],
    version = 1,
    exportSchema = true
)
internal abstract class InventoryDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun conditionDao(): AssetConditionDao
}