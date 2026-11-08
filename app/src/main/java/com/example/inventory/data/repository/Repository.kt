package com.example.inventory.data.repository

import com.example.inventory.core.repository.IAssetConditionsRepo
import com.example.inventory.core.repository.IAssetsRepo
import com.example.inventory.core.repository.IRepository
import javax.inject.Inject

internal class Repository @Inject constructor(
    private val assetsRepo: IAssetsRepo,
    private val conditionsRepo: IAssetConditionsRepo
) : IRepository {
    override fun getAssetsRepo(): IAssetsRepo = assetsRepo
    override fun getAssetConditionsRepo(): IAssetConditionsRepo = conditionsRepo
}