package com.example.inventory.di

import android.content.Context
import com.example.inventory.core.infrastructure.IBarcodeScannerService
import com.example.inventory.core.infrastructure.IPhotoService
import com.example.inventory.infrastructure.services.BarcodeScannerService
import com.example.inventory.infrastructure.services.PhotoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class InfrastructureModule {
    companion object {
        @Provides
        @Singleton
        fun provideBarcodeScannerService(
            @ApplicationContext context: Context
        ): IBarcodeScannerService {
            return BarcodeScannerService(context)
        }

        @Provides
        @Singleton
        fun providePhotoService(
            @ApplicationContext context: Context
        ): IPhotoService {
            return PhotoService(context)
        }
    }
}