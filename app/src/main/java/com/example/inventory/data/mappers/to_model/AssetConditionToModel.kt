package com.example.inventory.data.mappers.to_model

import com.example.inventory.data.database.entities.AssetConditionEntity
import com.example.inventory.domain.models.AssetCondition

internal fun AssetConditionEntity.toModel() = AssetCondition(
    code = code,
    name = name
)