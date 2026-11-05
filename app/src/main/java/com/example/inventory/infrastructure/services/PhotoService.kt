package com.example.inventory.infrastructure.services

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import com.example.inventory.R
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either
import com.example.inventory.core.functional.toLeft
import com.example.inventory.core.functional.toRight
import com.example.inventory.core.infrastructure.IPhotoService
import com.example.inventory.core.infrastructure.PhotoFile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

internal class PhotoService(
    private val context: Context
) : IPhotoService {
    override suspend fun createFile(): Either<Failure, PhotoFile> =
        onIo(R.string.media_error_create) {
            File.createTempFile("IMG_", ".jpg", photoDirectory())
                .asPhotoFile()
        }

    override suspend fun save(source: Uri): Either<Failure, PhotoFile> =
        onIo(R.string.media_error_import) {
            val resolver = context.contentResolver
            val mime = resolver.getType(source)
            require(mime?.startsWith("image/") == true) {
                context.getString(R.string.media_error_image_required)
            }
            val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(mime)
                ?.takeIf { it.matches(Regex("[a-zA-Z0-9]+")) }
                ?: "img"
            val file = File.createTempFile(
                "IMG_", ".$extension", photoDirectory()
            )
            try {
                val input = resolver.openInputStream(source)
                    ?: throw IOException(
                        context.getString(R.string.media_error_open_stream)
                    )
                input.use { sourceStream ->
                    file.outputStream().use { destination ->
                        sourceStream.copyTo(destination)
                    }
                }
                checkNotEmpty(file)
                file.asPhotoFile()
            } catch (e: Exception) {
                file.delete()
                throw e
            }
        }

    override suspend fun getFile(
        fileName: String
    ): Either<Failure, PhotoFile> = onIo(R.string.media_error_read) {
        val file = resolveFile(fileName)
        checkNotEmpty(file)
        file.asPhotoFile()
    }

    override suspend fun delete(
        fileName: String
    ): Either<Failure, Unit> = onIo(R.string.media_error_delete) {
        val file = resolveFile(fileName)
        if (file.exists() && !file.delete()) {
            throw IOException(
                context.getString(R.string.media_error_delete)
            )
        }
    }

    private fun photoDirectory(): File {
        val directory = File(context.filesDir, "photos")
        if (!directory.isDirectory && !directory.mkdirs()) {
            throw IOException(
                context.getString(R.string.media_error_directory)
            )
        }
        return directory
    }

    private fun resolveFile(fileName: String): File {
        val directory = photoDirectory().canonicalFile
        val file = File(directory, fileName).canonicalFile
        require(fileName.isNotBlank() && file.parentFile == directory) {
            context.getString(R.string.media_error_file_name)
        }
        return file
    }

    private fun checkNotEmpty(file: File) {
        if (!file.isFile || file.length() == 0L) {
            throw IOException(
                context.getString(R.string.media_error_empty_file)
            )
        }
    }

    private fun File.asPhotoFile() = PhotoFile(
        fileName = name,
        uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", this
        )
    )

    private suspend fun <T> onIo(
        @StringRes errorMessageRes: Int,
        action: () -> T
    ): Either<Failure, T> = withContext(Dispatchers.IO) {
        try {
            action().toRight()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Failure.MediaFailure(
                context.getString(errorMessageRes)
            ).toLeft()
        }
    }
}