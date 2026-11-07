package com.example.inventory.data.database.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "asset_conditions")
internal data class AssetConditionEntity(
    @PrimaryKey val code: String,
    val name: String
)