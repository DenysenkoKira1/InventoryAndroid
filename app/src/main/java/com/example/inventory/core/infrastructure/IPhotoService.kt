package com.example.inventory.core.infrastructure

import android.net.Uri
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either

data class PhotoFile(
    val fileName: String,
    val uri: Uri
)

interface IPhotoService {
    suspend fun createFile(): Either<Failure, PhotoFile>
    suspend fun save(source: Uri): Either<Failure, PhotoFile>
    suspend fun getFile(fileName: String): Either<Failure, PhotoFile>
    suspend fun delete(fileName: String): Either<Failure, Unit>
}