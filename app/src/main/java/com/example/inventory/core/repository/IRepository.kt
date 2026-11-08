package com.example.inventory.core.repository

interface IRepository {
    fun getAssetsRepo(): IAssetsRepo
    fun getAssetConditionsRepo(): IAssetConditionsRepo
}