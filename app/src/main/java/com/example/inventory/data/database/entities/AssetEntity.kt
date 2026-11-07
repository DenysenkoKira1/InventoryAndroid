package com.example.inventory.data.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "assets",
    foreignKeys = [ForeignKey(
        entity = AssetConditionEntity::class,
        parentColumns = ["code"],
        childColumns = ["condition_code"],
        onDelete = ForeignKey.RESTRICT
    )],
    indices = [
        Index(value = ["inventory_number"], unique = true),
        Index(value = ["serial_number"], unique = true),
        Index(value = ["condition_code"])
    ]
)
internal data class AssetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "inventory_number") val invNumber: String,
    @ColumnInfo(name = "serial_number") val srnNumber: String,
    val name: String,
    val location: String,
    val quantity: Int,
    val unit: String,
    @ColumnInfo(name = "condition_code") val conditionCode: String,
    val comment: String,
    @ColumnInfo(name = "photo_file_name") val photoFileName: String?,
    @ColumnInfo(name = "state", defaultValue = "0")
    val state: Boolean = false
)