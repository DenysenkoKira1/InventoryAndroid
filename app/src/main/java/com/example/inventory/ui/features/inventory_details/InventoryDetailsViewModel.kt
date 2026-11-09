package com.example.inventory.ui.features.inventory_details

import android.app.Application
import com.example.inventory.R
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.Either
import com.example.inventory.core.infrastructure.IPhotoService
import com.example.inventory.core.repository.IRepository
import com.example.inventory.ui.features.base.BaseViewModel
import com.example.inventory.ui.features.inventory_details.items.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class InventoryDetailsViewModel @Inject constructor(
    application: Application,
    private val repo: IRepository,
    private val photoService: IPhotoService,
    ) : BaseViewModel(application) {
    private val _viewState = MutableStateFlow(InventoryDetailsState())
    val viewState = _viewState.asStateFlow()

    suspend fun loadData(assetId: Long) {
        startLoading()
        _viewState.value = InventoryDetailsState()
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
                showMessage(R.string.details_asset_missing)
                return
            }
            if (!asset.state) {
                showMessage(R.string.details_not_checked)
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
            val conditionName = conditions.firstOrNull {
                it.code == asset.conditionCode
            }?.name ?: context.getString(
                R.string.details_unknown_condition, asset.conditionCode
            )
            _viewState.update { it.copy(
                asset = asset, conditionName = conditionName
            ) }
            val photoName = asset.photoFileName
            if (photoName != null) {
                when (val result = photoService.getFile(photoName)) {
                    is Either.Left -> {
                        handleFailure(result.a)
                    }
                    is Either.Right -> {
                        _viewState.update {
                            it.copy(photoUri = result.b.uri)
                        }
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showError(checkingExceptionOnNull(e))
        } finally {
            stopLoading()
        }
    }

    private fun showMessage(messageResId: Int) {
        showBasicDialog(
            title = context.getString(R.string.details_error_title),
            message = context.getString(messageResId),
            isSuccess = false
        )
    }

    private fun handleFailure(failure: Failure) {
        when (failure) {
            Failure.AssetNotFound ->
                showMessage(R.string.details_asset_missing)
            is Failure.DbError -> showError(failure.message)
            is Failure.MediaFailure -> showError(failure.message)
            else -> showError(failure.toString())
        }
    }

    private fun showError(details: String) {
        showBasicDialog(
            title = context.getString(R.string.details_error_title),
            message = context.getString(
                R.string.details_error_details,
                details.ifBlank {
                    context.getString(R.string.photo_error_unknown)
                }
            ),
            isSuccess = false
        )
    }
}