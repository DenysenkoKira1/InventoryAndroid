package com.example.inventory.ui.features.inventory_edit

import android.app.Application
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.inventory.R
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.eitherSuspend
import com.example.inventory.core.infrastructure.IPhotoService
import com.example.inventory.core.infrastructure.PhotoFile
import com.example.inventory.core.repository.IRepository
import com.example.inventory.domain.models.Asset
import com.example.inventory.ui.features.base.BaseViewModel
import com.example.inventory.ui.features.inventory_edit.items.InventoryEditState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryEditViewModel @Inject constructor(
    application: Application,
    private val repo: IRepository,
    private val photoService: IPhotoService,
    private val savedState: SavedStateHandle,
    ) : BaseViewModel(application) {
    private val _viewState = MutableStateFlow(InventoryEditState(
        photoFileName = savedState[PHOTO],
        cameraUri = savedState.get<String>(CAMERA_URI)?.let(Uri::parse),
        isCameraLaunchRequested = savedState[CAMERA_REQUEST] ?: false,
        isMediaOperationRunning =
            savedState.get<String>(CAMERA_NAME) != null
    ))
    val viewState = _viewState.asStateFlow()
    private var mediaJob: Job? = null

    suspend fun loadData(assetId: Long) {
        startLoading()
        try {
            repo.getAssetsRepo().getById(assetId).eitherSuspend(
                fnL = { handleFailure(it) },
                fnR = { asset ->
                    if (asset == null) {
                        handleFailure(Failure.AssetNotFound)
                    } else {
                        if (!savedState.contains(ASSET_ID)) {
                            savedState[ASSET_ID] = asset.id
                            savedState[ORIGINAL] = asset.photoFileName
                            savedState[PHOTO] = asset.photoFileName
                        }
                        check(savedState.get<Long>(ASSET_ID) == asset.id)
                        _viewState.update { it.copy(asset = asset) }
                        refreshPhoto()
                        if (mediaJob?.isActive != true) {
                            val importUri =
                                savedState.get<String>(IMPORT_URI)
                            when {
                                savedState.contains(CAMERA_RESULT) -> {
                                    onCameraResult(
                                        savedState[CAMERA_RESULT]
                                            ?: false
                                    )
                                }
                                importUri != null ->
                                    onGalleryResult(Uri.parse(importUri))
                            }
                        }
                    }
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showPhotoError(checkingExceptionOnNull(e))
        } finally {
            stopLoading()
        }
    }

    fun showPhotoSource() {
        if (viewState.value.isMediaOperationRunning) return
        _viewState.update { it.copy(isPhotoSourceDialogVisible = true) }
    }

    fun hidePhotoSource() {
        _viewState.update { it.copy(isPhotoSourceDialogVisible = false) }
    }

    fun prepareCamera() {
        hidePhotoSource()
        if (viewState.value.isMediaOperationRunning) return
        runMedia {
            photoService.createFile().eitherSuspend(
                fnL = { handleFailure(it) },
                fnR = { file ->
                    track(file.fileName)
                    savedState[CAMERA_NAME] = file.fileName
                    savedState[CAMERA_URI] = file.uri.toString()
                    savedState[CAMERA_REQUEST] = true
                    _viewState.update {
                        it.copy(
                            cameraUri = file.uri,
                            isCameraLaunchRequested = true
                        )
                    }
                }
            )
        }
    }

    fun cameraLaunchHandled() {
        savedState[CAMERA_REQUEST] = false
        _viewState.update { it.copy(isCameraLaunchRequested = false) }
    }

    fun onCameraResult(success: Boolean) {
        savedState[CAMERA_RESULT] = success
        runMedia {
            try {
                val name = savedState.get<String>(CAMERA_NAME)
                if (success && name != null) {
                    photoService.getFile(name).eitherSuspend(
                        fnL = { handleFailure(it) },
                        fnR = { acceptPhoto(it) }
                    )
                }
            } finally {
                clearCamera()
                cleanupDrafts()
            }
        }
    }

    fun galleryStarted(): Boolean {
        hidePhotoSource()
        if (viewState.value.isMediaOperationRunning) return false
        _viewState.update { it.copy(isMediaOperationRunning = true) }
        return true
    }

    fun onGalleryResult(uri: Uri?) {
        if (uri == null) {
            _viewState.update { it.copy(isMediaOperationRunning = false) }
            return
        }
        savedState[IMPORT_URI] = uri.toString()
        runMedia {
            try {
                photoService.save(uri).eitherSuspend(
                    fnL = { handleFailure(it) },
                    fnR = { acceptPhoto(it) }
                )
            } finally {
                savedState.remove<String>(IMPORT_URI)
            }
        }
    }

    fun onLaunchFailed(error: Exception, wasCamera: Boolean) {
        showPhotoError(checkingExceptionOnNull(error))
        if (wasCamera) onCameraResult(false) else onGalleryResult(null)
    }

    fun removePhoto() {
        if (viewState.value.isMediaOperationRunning) return
        runMedia {
            savedState[PHOTO] = null
            _viewState.update {
                it.copy(photoFileName = null, photoUri = null)
            }
            cleanupDrafts()
        }
    }

    fun withCurrentPhoto(asset: Asset): Asset =
        asset.copy(photoFileName = savedState[PHOTO])

    suspend fun onAssetSaved(asset: Asset): Boolean {
        check(asset.id == viewState.value.asset?.id)
        val oldName = savedState.get<String>(ORIGINAL)
        savedState[ORIGINAL] = asset.photoFileName
        savedState[PHOTO] = asset.photoFileName
        savedState[DRAFTS] = ArrayList(
            drafts().filter { it != asset.photoFileName }
        )
        if (oldName != null && oldName != asset.photoFileName) {
            track(oldName)
        }
        _viewState.update {
            it.copy(asset = asset, photoFileName = asset.photoFileName)
        }
        return cleanupDrafts()
    }

    suspend fun discardPhotoChanges(): Boolean {
        if (viewState.value.isMediaOperationRunning) return false
        _viewState.update { it.copy(isMediaOperationRunning = true) }
        return try {
            savedState[PHOTO] = savedState.get<String>(ORIGINAL)
            clearCamera()
            val result = cleanupDrafts()
            refreshPhoto()
            result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showPhotoError(checkingExceptionOnNull(e))
            false
        } finally {
            _viewState.update { it.copy(isMediaOperationRunning = false) }
        }
    }

    private fun runMedia(action: suspend () -> Unit) {
        if (mediaJob?.isActive == true) return
        _viewState.update { it.copy(isMediaOperationRunning = true) }
        mediaJob = viewModelScope.launch {
            try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showPhotoError(checkingExceptionOnNull(e))
            } finally {
                _viewState.update { it.copy(
                    isMediaOperationRunning =
                        savedState.get<String>(CAMERA_NAME) != null
                ) }
            }
        }
    }

    private suspend fun acceptPhoto(file: PhotoFile) {
        track(file.fileName)
        savedState[PHOTO] = file.fileName
        _viewState.update {
            it.copy(photoFileName = file.fileName, photoUri = file.uri)
        }
        cleanupDrafts()
    }

    private suspend fun refreshPhoto() {
        val name = savedState.get<String>(PHOTO)
        _viewState.update { it.copy(photoFileName = name, photoUri = null) }
        if (name != null) {
            photoService.getFile(name).eitherSuspend(
                fnL = { failure ->
                    if (savedState.get<String>(PHOTO) == name) {
                        handleFailure(failure)
                    }
                },
                fnR = { file ->
                    if (savedState.get<String>(PHOTO) == name) {
                        _viewState.update { it.copy(photoUri = file.uri) }
                    }
                }
            )
        }
    }

    private fun drafts(): List<String> =
        savedState.get<ArrayList<String>>(DRAFTS)?.toList().orEmpty()

    private fun track(name: String) {
        savedState[DRAFTS] = ArrayList((drafts() + name).distinct())
    }

    private suspend fun cleanupDrafts(): Boolean {
        val keep = setOf(
            savedState.get<String>(ORIGINAL),
            savedState.get<String>(PHOTO),
            savedState.get<String>(CAMERA_NAME)
        )
        var success = true
        for (name in drafts().filter { it !in keep }) {
            photoService.delete(name).either(
                fnL = { failure ->
                    success = false
                    handleFailure(failure)
                },
                fnR = { savedState[DRAFTS] = ArrayList(drafts() - name) }
            )
        }
        return success
    }

    private fun clearCamera() {
        savedState.remove<String>(CAMERA_NAME)
        savedState.remove<String>(CAMERA_URI)
        savedState.remove<Boolean>(CAMERA_REQUEST)
        savedState.remove<Boolean>(CAMERA_RESULT)
        _viewState.update {
            it.copy(cameraUri = null, isCameraLaunchRequested = false)
        }
    }

    private fun handleFailure(failure: Failure) {
        val message = when (failure) {
            is Failure.MediaFailure -> failure.message
            is Failure.DbError -> failure.message
            Failure.AssetNotFound ->
                context.getString(R.string.photo_asset_missing)
            else -> failure.toString()
        }
        showPhotoError(message)
    }

    private fun showPhotoError(details: String) {
        showBasicDialog(
            title = context.getString(R.string.photo_error_title),
            message = context.getString(
                R.string.photo_error_details,
                details.ifBlank {
                    context.getString(R.string.photo_error_unknown)
                }
            ),
            isSuccess = false
        )
    }

    private companion object {
        const val ASSET_ID = "photo_asset_id"
        const val ORIGINAL = "photo_original"
        const val PHOTO = "photo_current"
        const val DRAFTS = "photo_drafts"
        const val CAMERA_NAME = "photo_camera_name"
        const val CAMERA_URI = "photo_camera_uri"
        const val CAMERA_REQUEST = "photo_camera_request"
        const val CAMERA_RESULT = "photo_camera_result"
        const val IMPORT_URI = "photo_import_uri"
    }
}