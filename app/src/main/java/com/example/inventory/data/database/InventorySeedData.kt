package com.example.inventory.data.database

import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.example.inventory.data.database.entities.AssetEntity

internal class InventoryDatabaseCallback : RoomDatabase.Callback() {
    override suspend fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        InventorySeedData.insert(connection)
    }
}

internal object InventorySeedData {
    private val conditions = listOf(
        "ACT" to "В наявності",
        "MIS" to "Не знайдено",
        "REP" to "Потребує перевірки"
    )

    private val assets = listOf(
        asset(1, "INV-100001", "111",
            "Монітор LG UltraWide 29 Робоче місце бухгалтера",
            "Київ головний склад 1000 KYIV-01",
            "ACT"),
        asset(2, "INV-100002", "222",
            "Проєктор Epson EB-W06 Навчальна аудиторія",
            "Київ головний склад 1000 KYIV-01",
            "REP"),
        asset(3, "INV-100003", "333",
            "Маршрутизатор MikroTik hAP Кабінет адміністратора",
            "Київ головний склад 1000 KYIV-01",
            "MIS"),
        asset(4, "INV-100004", "444",
            "Джерело безперебійного живлення APC Smart-UPS 1500",
            "Київ резервний майданчик 1000 KYIV-02",
            "ACT")
    )

    fun insert(connection: SQLiteConnection) {
        conditions.forEach { (code, name) ->
            connection.prepare(
                "INSERT INTO asset_conditions (code, name) VALUES (?, ?)"
            ).use { statement ->
                statement.bindText(1, code)
                statement.bindText(2, name)
                statement.step()
            }
        }

        assets.forEach { asset ->
            connection.prepare(
                """INSERT INTO assets ( 
                    id, inventory_number, serial_number, name, location, 
                    quantity, unit, condition_code, comment, 
                    photo_file_name, State 
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
            ).use { statement ->
                statement.bindLong(1, asset.id)
                statement.bindText(2, asset.invNumber)
                statement.bindText(3, asset.srnNumber)
                statement.bindText(4, asset.name)
                statement.bindText(5, asset.location)

                statement.bindLong(6, asset.quantity.toLong())
                statement.bindText(7, asset.unit)
                statement.bindText(8, asset.conditionCode)
                statement.bindText(9, asset.comment)

                val photoFileName = asset.photoFileName
                if (photoFileName == null) {
                    statement.bindNull(10)
                } else {
                    statement.bindText(10, photoFileName)
                }

                statement.bindLong(11, if (asset.state) 1L else 0L)
                statement.step()
            }
        }
    }

    private fun asset(
        id: Long,
        inv: String,
        serial: String,
        name: String,
        location: String,
        condition: String
    ) = AssetEntity(
        id = id,
        invNumber = inv,
        srnNumber = serial,
        name = name,
        location = location,
        quantity = 1,
        unit = "EA",
        conditionCode = condition,
        comment = "",
        photoFileName = null,
        state = false
    )
}