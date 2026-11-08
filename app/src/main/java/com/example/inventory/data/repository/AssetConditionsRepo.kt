package com.example.inventory.data.repository

import android.database.SQLException
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either
import com.example.inventory.core.repository.IAssetConditionsRepo
import com.example.inventory.data.database.dao.AssetConditionDao
import com.example.inventory.data.mappers.to_model.toModel
import com.example.inventory.domain.models.AssetCondition
import javax.inject.Inject

internal class AssetConditionsRepo @Inject constructor(
    private val dao: AssetConditionDao
) : IAssetConditionsRepo {
    override suspend fun getAll(): Either<Failure, List<AssetCondition>> = try {
        Either.Right(dao.getAll().map { it.toModel() })
    } catch (error: SQLException) {
        Either.Left(Failure.DbError(error.message.orEmpty()))
    }
}