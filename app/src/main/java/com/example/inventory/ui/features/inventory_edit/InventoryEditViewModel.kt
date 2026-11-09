package com.example.inventory.ui.features.inventory_edit

import android.app.Application
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.inventory.R
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either
import com.example.inventory.core.functional.eitherSuspend
import com.example.inventory.core.infrastructure.IPhotoService
import com.example.inventory.core.infrastructure.PhotoFile
import com.example.inventory.core.repository.IRepository
import com.example.inventory.domain.models.Asset
import com.example.inventory.domain.models.AssetCondition
import com.example.inventory.ui.features.base.BaseViewModel
import com.example.inventory.ui.features.inventory_edit.items.*
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
            savedState.get<String>(CAMERA_NAME) != null,
        isSaved = savedState[SAVED] ?: false
    ))
    val viewState = _viewState.asStateFlow()
    private var mediaJob: Job? = null

    suspend fun loadData(assetId: Long, mode: InventoryEditMode) {
        if (viewState.value.asset != null) return
        startLoading()
        try {
            val asset = when (val result =
                repo.getAssetsRepo().getById(assetId)) {
                is Either.Left -> {
                    handleFailure(result.a)
                    return
                }
                is Either.Right -> result.b
            }
            if (asset == null) {
                handleFailure(Failure.AssetNotFound)
                return
            }
            val conditions = when (val result =
                repo.getAssetConditionsRepo().getAll()) {
                is Either.Left -> {
                    handleFailure(result.a)
                    return
                }
                is Either.Right -> result.b
            }
            initState(asset, mode, conditions)
            refreshPhoto()
            if (viewState.value.isSaved) {
                showSavedMessage()
            } else if (mediaJob?.isActive != true) {
                val importUri = savedState.get<String>(IMPORT_URI)
                when {
                    savedState.contains(CAMERA_RESULT) -> {
                        onCameraResult(savedState[CAMERA_RESULT] ?: false)
                    }
                    importUri != null -> {
                        onGalleryResult(Uri.parse(importUri))
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showEditError(checkingExceptionOnNull(e))
        } finally {
            stopLoading()
        }
    }

    private fun initState(
        asset: Asset,
        mode: InventoryEditMode,
        conditions: List<AssetCondition>,
        ) {
        if (!savedState.contains(ASSET_ID)) {
            savedState[ASSET_ID] = asset.id
            savedState[PHOTO] = asset.photoFileName
        }
        check(savedState.get<Long>(ASSET_ID) == asset.id)
        val oldOriginal = savedState.get<String>(ORIGINAL)
        savedState[ORIGINAL] = asset.photoFileName
        savedState[DRAFTS] = ArrayList(
            drafts().filter { it != asset.photoFileName }
        )
        if (oldOriginal != null && oldOriginal != asset.photoFileName) {
            track(oldOriginal)
        }
        if (!savedState.contains(FORM_READY)) {
            savedState[MODE] = mode.name
            savedState[NAME] = asset.name
            savedState[INVENTORY] = asset.invNumber
            savedState[SERIAL] = asset.srnNumber
            savedState[COUNT] = asset.quantity.toString()
            savedState[COMMENT] = asset.comment
            savedState[CONDITION] =
                if (asset.state) asset.conditionCode else ""
            savedState[FORM_READY] = true
        }
        check(savedState.get<String>(MODE) == mode.name)
        val code = savedState.get<String>(CONDITION).orEmpty()
        _viewState.update { it.copy(
            asset = asset,
            mode = mode,
            name = savedState.get<String>(NAME).orEmpty(),
            inventoryNumber = savedState.get<String>(INVENTORY).orEmpty(),
            serialNumber = savedState.get<String>(SERIAL).orEmpty(),
            count = savedState.get<String>(COUNT).orEmpty(),
            comment = savedState.get<String>(COMMENT).orEmpty(),
            inventoryStates = conditions,
            savedConditionCode = code,
            selectedConditionCode = code
        ) }
    }

    private fun canEdit(): Boolean =
        viewState.value.asset != null &&
                baseViewState.value.isDataLoaded &&
                !viewState.value.isMediaOperationRunning &&
                !viewState.value.isSaving && !viewState.value.isSaved

    fun onNameChanged(value: String) {
        if (!canEdit()) return
        if (viewState.value.mode != InventoryEditMode.FULL) return
        savedState[NAME] = value.take(MAX_NAME_LENGTH)
        _viewState.update { it.copy(name = savedState[NAME] ?: "") }
    }

    fun onInventoryNumberChanged(value: String) {
        if (!canEdit()) return
        if (viewState.value.mode != InventoryEditMode.FULL) return
        savedState[INVENTORY] = value
        _viewState.update { it.copy(inventoryNumber = value) }
    }

    fun onSerialNumberChanged(value: String) {
        if (!canEdit()) return
        if (viewState.value.mode != InventoryEditMode.FULL) return
        savedState[SERIAL] = value
        _viewState.update { it.copy(serialNumber = value) }
    }

    fun onCountChanged(value: String) {
        if (!canEdit() || value.length > 10 ||
            !value.all { it in '0'..'9' }) return
        savedState[COUNT] = value
        _viewState.update { it.copy(count = value) }
    }

    fun onCommentChanged(value: String) {
        if (!canEdit()) return
        savedState[COMMENT] = value
        _viewState.update { it.copy(comment = value) }
    }

    fun onObjectStateClick() {
        if (!canEdit()) return
        _viewState.update { it.copy(
            selectedConditionCode = it.savedConditionCode,
            isBottomSheetOpened = true
        ) }
    }

    fun onSelectInventoryState(condition: AssetCondition) {
        if (!canEdit()) return
        if (viewState.value.inventoryStates.none {
                it.code == condition.code
            }) return
        _viewState.update {
            it.copy(selectedConditionCode = condition.code)
        }
    }

    fun onSaveInventoryState() {
        if (!canEdit()) return
        val code = viewState.value.selectedConditionCode
        if (viewState.value.inventoryStates.none { it.code == code }) return
        savedState[CONDITION] = code
        _viewState.update { it.copy(
            savedConditionCode = code, isBottomSheetOpened = false
        ) }
    }

    fun onCloseBottomSheet() {
        _viewState.update { it.copy(
            selectedConditionCode = it.savedConditionCode,
            isBottomSheetOpened = false
        ) }
    }

    fun isEvidenceRequired(): Boolean =
        viewState.value.mode == InventoryEditMode.FULL

    fun isSaveAvailable(): Boolean =
        canEdit() && validationFailure() == null

    private fun validationFailure(): Failure? {
        val state = viewState.value
        if (state.mode == InventoryEditMode.FULL) {
            if (state.name.isBlank() ||
                state.name.length > MAX_NAME_LENGTH) {
                return Failure.InvalidName
            }
            if (state.inventoryNumber.isBlank()) {
                return Failure.EmptyInventoryNumber
            }
            if (state.serialNumber.isBlank()) {
                return Failure.EmptySerialNumber
            }
        }
        val count = state.count.toIntOrNull()
        if (count == null || count <= 0) return Failure.InvalidQuantity
        if (state.inventoryStates.none {
                it.code == state.savedConditionCode
            }) {
            return Failure.InvalidCondition
        }
        if (isEvidenceRequired() && state.comment.isBlank()) {
            return Failure.CommentRequired
        }
        if (isEvidenceRequired() && state.photoFileName == null) {
            return Failure.PhotoRequired
        }
        return null
    }

    fun onSave() {
        if (!canEdit()) return
        validationFailure()?.let { handleFailure(it); return }
        val state = viewState.value
        val original = state.asset ?: return
        val updated = withCurrentPhoto(original.copy(
            name = if (state.mode == InventoryEditMode.FULL)
                state.name.trim() else original.name,
            invNumber = if (state.mode == InventoryEditMode.FULL)
                state.inventoryNumber.trim() else original.invNumber,
            srnNumber = if (state.mode == InventoryEditMode.FULL)
                state.serialNumber.trim() else original.srnNumber,
            quantity = state.count.toInt(),
            conditionCode = state.savedConditionCode,
            comment = state.comment.trim(),
            state = true
        ))
        _viewState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                val photoName = updated.photoFileName
                if (photoName != null) {
                    val result = photoService.getFile(photoName)
                    if (result is Either.Left) {
                        handleFailure(result.a)
                        return@launch
                    }
                }
                when (val result = repo.getAssetsRepo().update(updated)) {
                    is Either.Left -> handleFailure(result.a)
                    is Either.Right -> {
                        savedState[SAVED] = true
                        _viewState.update { it.copy(isSaved = true) }
                        if (onAssetSaved(result.b)) {
                            showSavedMessage()
                        } else {
                            showSavedCleanupWarning(
                                baseViewState.value.messageAppBasicDialog
                            )
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = checkingExceptionOnNull(e)
                if (viewState.value.isSaved) {
                    showSavedCleanupWarning(message)
                } else {
                    showEditError(message)
                }
            } finally {
                _viewState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun showSavedMessage() {
        showBasicDialog(
            title = context.getString(R.string.edit_success_title),
            message = context.getString(R.string.edit_saved),
            isSuccess = true
        )
    }

    private fun showSavedCleanupWarning(details: String) {
        showBasicDialog(
            title = context.getString(R.string.edit_cleanup_title),
            message = context.getString(
                R.string.edit_cleanup_details, details
            ),
            isSuccess = false
        )
    }

    private fun showEditError(details: String) {
        showBasicDialog(
            title = context.getString(R.string.edit_error_title),
            message = context.getString(
                R.string.edit_error_details, details
            ),
            isSuccess = false
        )
    }

    fun showPhotoSource() {
        if (!canEdit()) return
        _viewState.update { it.copy(isPhotoSourceDialogVisible = true) }
    }

    fun hidePhotoSource() {
        _viewState.update { it.copy(isPhotoSourceDialogVisible = false) }
    }

    fun prepareCamera() {
        hidePhotoSource()
        if (!canEdit()) return
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
        if (!canEdit()) return false
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
        if (!canEdit()) return
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
        if (viewState.value.isSaved) return true
        if (viewState.value.isMediaOperationRunning ||
            viewState.value.isSaving) return false
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
        val resId = when (failure) {
            Failure.AssetNotFound -> R.string.edit_asset_missing
            Failure.InvalidName -> R.string.edit_invalid_name
            Failure.EmptyInventoryNumber -> R.string.edit_empty_inventory
            Failure.EmptySerialNumber -> R.string.edit_empty_serial
            Failure.InvalidQuantity -> R.string.edit_invalid_quantity
            Failure.InvalidCondition -> R.string.edit_invalid_condition
            Failure.CommentRequired -> R.string.edit_comment_required
            Failure.PhotoRequired -> R.string.edit_photo_required
            Failure.DuplicateInventoryNumber ->
                R.string.edit_duplicate_inventory
            Failure.DuplicateSerialNumber -> R.string.edit_duplicate_serial
            else -> null
        }
        when {
            failure is Failure.MediaFailure ->
                showPhotoError(failure.message)
            failure is Failure.DbError -> showEditError(failure.message)
            resId != null -> showBasicDialog(
                title = context.getString(R.string.edit_error_title),
                message = context.getString(resId),
                isSuccess = false
            )
            else -> showEditError(failure.toString())
        }
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
        const val MAX_NAME_LENGTH = 100
        const val FORM_READY = "edit_form_ready"
        const val MODE = "edit_mode"
        const val NAME = "edit_name"
        const val INVENTORY = "edit_inventory"
        const val SERIAL = "edit_serial"
        const val COUNT = "edit_count"
        const val COMMENT = "edit_comment"
        const val CONDITION = "edit_condition"
        const val SAVED = "edit_saved"
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