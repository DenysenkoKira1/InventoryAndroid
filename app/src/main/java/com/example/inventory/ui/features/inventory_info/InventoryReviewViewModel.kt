package com.example.inventory.ui.features.inventory_info

import android.app.Application
import com.example.inventory.R
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.eitherSuspend
import com.example.inventory.core.repository.IRepository
import com.example.inventory.ui.features.base.BaseViewModel
import com.example.inventory.ui.features.inventory_info.items.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InventoryReviewViewModel @Inject constructor(
    application: Application,
    private val repo: IRepository,
    ) : BaseViewModel(application) {

    private val _viewState = MutableStateFlow(InventoryReviewState())
    val viewState = _viewState.asStateFlow()

    suspend fun loadData(assetId: Long) {
        startLoading()
        hideBasicDialog()
        _viewState.value = InventoryReviewState()
        try {
            withContext(Dispatchers.IO) {
                showAssetInfo(assetId)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showLoadError()
        } finally {
            stopLoading()
        }
    }

    private suspend fun showAssetInfo(assetId: Long) {
        repo.getAssetsRepo().getById(assetId).eitherSuspend(
            fnL = { failure -> handleFailure(failure) },
            fnR = { asset ->
                if (asset == null) {
                    handleFailure(Failure.AssetNotFound)
                } else {
                    repo.getAssetConditionsRepo().getAll().eitherSuspend(
                        fnL = { failure -> handleFailure(failure) },
                        fnR = { conditions ->
                            val condition = conditions.firstOrNull {
                                it.code == asset.conditionCode
                            }
                            _viewState.update {
                                it.copy(
                                    asset = asset,
                                    conditionName = condition?.name
                                        ?: context.getString(
                                            R.string.review_value_missing
                                        )
                                )
                            }
                        }
                    )
                }
            }
        )
    }

    private fun handleFailure(failure: Failure) {
        if (failure == Failure.AssetNotFound) {
            showBasicDialog(
                title = context.getString(R.string.review_error_title),
                message = context.getString(
                    R.string.review_asset_not_found
                ),
                isSuccess = false
            )
        } else {
            showLoadError()
        }
    }

    private fun showLoadError() {
        showBasicDialog(
            title = context.getString(R.string.review_error_title),
            message = context.getString(R.string.review_load_error),
            isSuccess = false
        )
    }
}