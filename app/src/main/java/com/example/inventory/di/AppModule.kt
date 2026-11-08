package com.example.inventory.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.example.inventory.data.database.InventoryDatabase
import com.example.inventory.data.database.InventoryDatabaseCallback
import com.example.inventory.data.database.dao.AssetConditionDao
import com.example.inventory.data.database.dao.AssetDao
import com.example.inventory.core.repository.IAssetConditionsRepo
import com.example.inventory.core.repository.IAssetsRepo
import com.example.inventory.core.repository.IRepository
import com.example.inventory.data.repository.AssetConditionsRepo
import com.example.inventory.data.repository.AssetsRepo
import com.example.inventory.data.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindAssetsRepo(impl: AssetsRepo): IAssetsRepo

    @Binds
    @Singleton
    abstract fun bindConditionsRepo(impl: AssetConditionsRepo): IAssetConditionsRepo

    @Binds
    @Singleton
    abstract fun bindRepository(impl: Repository): IRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): InventoryDatabase = Room.databaseBuilder<InventoryDatabase>(
            context,
            "inventory.db"
        )
            .setDriver(AndroidSQLiteDriver())
            .addCallback(InventoryDatabaseCallback())
            .build()

        @Provides
        fun provideAssetDao(db: InventoryDatabase): AssetDao = db.assetDao()

        @Provides
        fun provideConditionDao(db: InventoryDatabase): AssetConditionDao =
            db.conditionDao()
    }
}