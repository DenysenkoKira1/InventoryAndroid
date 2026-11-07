package com.example.inventory.data.mappers.to_model

import com.example.inventory.data.database.entities.AssetEntity
import com.example.inventory.domain.models.Asset

internal fun AssetEntity.toModel() = Asset(
    id = id,
    invNumber = invNumber,
    srnNumber = srnNumber,
    name = name,
    location = location,
    state = state,

    quantity = quantity,
    unit = unit,
    conditionCode = conditionCode,
    comment = comment,
    photoFileName = photoFileName
)