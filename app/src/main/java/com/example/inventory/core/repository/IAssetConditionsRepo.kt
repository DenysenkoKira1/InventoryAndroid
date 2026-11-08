package com.example.inventory.core.repository

import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either
import com.example.inventory.domain.models.AssetCondition

interface IAssetConditionsRepo {
    suspend fun getAll(): Either<Failure, List<AssetCondition>>
}