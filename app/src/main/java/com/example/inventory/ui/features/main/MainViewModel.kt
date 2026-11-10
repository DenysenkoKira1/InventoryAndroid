package com.example.inventory.ui.features.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.inventory.R
import com.example.inventory.core.exceptions.Failure
import com.example.inventory.core.functional.eitherSuspend
import com.example.inventory.core.infrastructure.IBarcodeScannerService
import com.example.inventory.core.repository.IRepository
import com.example.inventory.domain.models.Asset
import com.example.inventory.ui.features.base.BaseViewModel
import com.example.inventory.ui.features.main.items.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val barcodeScanner: IBarcodeScannerService,
    private val repo: IRepository,
    ) : BaseViewModel(application) {

    private val _viewState: MutableStateFlow<MainState> =
        MutableStateFlow(MainState())
    val viewState = _viewState.asStateFlow()
    private var loadDataJob: Job? = null

    fun loadData() {
        if (loadDataJob?.isActive == true) return
        loadDataJob = viewModelScope.launch(Dispatchers.IO) {
            startLoading()
//            delay(3000)
            var isFirstResult = true
            try {
                repo.getAssetsRepo().observeAll().collect { result ->
                    result.either(
                        ::handleFailure,
                        ::handleSuccessLoadData
                    )
                    if (isFirstResult) {
                        stopLoading()
                        isFirstResult = false
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = checkingExceptionOnNull(e)
                showBasicDialog(
                    title = context.getString(R.string.main_error_title),
                    message = context.getString(
                        R.string.main_load_error_details,
                        message
                    ),
                    isSuccess = false
                )
            } finally {
                stopLoading()
            }
        }
    }

    private fun handleSuccessLoadData(assets: List<Asset>) {
        val (checked, unchecked) = assets.partition { it.state }
        _viewState.update {
            it.copy(
                uncheckedAssets = unchecked,
                checkedAssets = checked
            )
        }
    }

    fun onTabSelected(index: Int) {
        _viewState.update { it.copy(selectedTabIndex = index) }
    }

    fun scan() {
        if (!baseViewState.value.isDataLoaded ||
            viewState.value.isOperationRunning) return

        _viewState.update { it.copy(isOperationRunning = true) }
        viewModelScope.launch {
            startLoading()
            try {
                barcodeScanner.startScan().eitherSuspend(
                    ::handleBarcodeScannerFailure,
                    ::handleBarcodeScannerSuccess
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = checkingExceptionOnNull(e)
                showBasicDialog(
                    title = context.getString(R.string.main_scan_error_title),
                    message = context.getString(
                        R.string.main_scan_error_details,
                        message
                    ),
                    isSuccess = false
                )
            } finally {
                _viewState.update { it.copy(isOperationRunning = false) }
                stopLoading()
            }
        }
    }

    private suspend fun handleBarcodeScannerSuccess(result: String) {
        if (result.isBlank()) {
            showBasicDialog(
                title = context.getString(R.string.main_scan_error_title),
                message = context.getString(R.string.main_scan_empty),
                isSuccess = false
            )
            return
        }

        val inventoryNumber = result.trim()
        repo.getAssetsRepo().findByInventoryNumber(inventoryNumber).either(
            ::handleFailure,
            { asset ->
                if (asset != null) {
                    _viewState.update {
                        it.copy(
                            selectedAsset = asset,
                            isGoToInventoryReviewScreen = true
                        )
                    }
                } else {
                    showBasicDialog(
                        title = context.getString(R.string.main_scan_not_found_title),
                        message = context.getString(
                            R.string.main_scan_not_found,
                            inventoryNumber
                        ),
                        isSuccess = false
                    )
                }
            }
        )
    }

    private fun handleBarcodeScannerFailure(failure: Failure) {
        when (failure) {
            Failure.BarcodeScannerCancel -> Unit
            is Failure.BarcodeScannerFailure -> showBasicDialog(
                title = context.getString(R.string.main_scan_error_title),
                message = context.getString(
                    R.string.main_scan_error_details,
                    failure.message.ifBlank { failure.toString() }
                ),
                isSuccess = false
            )
            else -> handleFailure(failure)
        }
    }

    fun markNavigationHandled() {
        _viewState.update {
            it.copy(
                isGoToInventoryReviewScreen = false
            )
        }
    }

    fun onCardClick(asset: Asset) {
        if (!asset.state || !baseViewState.value.isDataLoaded ||
            viewState.value.isOperationRunning) return
        _viewState.update { it.copy(
            selectedAsset = asset,
            isGoToInventoryReviewScreen = true
        ) }
    }

    fun onClickClear() {
        if (!baseViewState.value.isDataLoaded ||
            viewState.value.isOperationRunning ||
            baseViewState.value.isShowAppConfirmActionDialog) return
        showConfirmActionDialog(
            title = context.getString(R.string.main_clear_title),
            message = context.getString(R.string.main_clear_question),
            onConfirm = {
                if (baseViewState.value.isShowAppConfirmActionDialog) {
                    hideConfirmActionDialog()
                    clearInventory()
                }
            }
        )
    }

    private fun clearInventory() {
        if (!baseViewState.value.isDataLoaded ||
            viewState.value.isOperationRunning) return
        _viewState.update { it.copy(isOperationRunning = true) }
        viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            try {
                repo.getAssetsRepo().clearInventoryState().either(
                    ::handleFailure,
                    { count ->
                        val messageResId = if (count == 0) {
                            R.string.main_clear_nothing
                        } else {
                            R.string.main_clear_success
                        }
                        showBasicDialog(
                            title = context.getString(
                                R.string.main_success_title
                            ),
                            message = context.getString(messageResId),
                            isSuccess = true
                        )
                    }
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = checkingExceptionOnNull(e)
                showBasicDialog(
                    title = context.getString(R.string.main_error_title),
                    message = context.getString(
                        R.string.main_clear_error_details, message
                    ),
                    isSuccess = false
                )
            } finally {
                _viewState.update { it.copy(isOperationRunning = false) }
                stopLoading()
            }
        }
    }

    private fun handleFailure(failure: Failure) {
        val message = when (failure) {
            is Failure.DbError -> context.getString(
                R.string.main_db_error_details,
                failure.message.ifBlank { failure.toString() }
            )
            else -> context.getString(
                R.string.main_operation_error_details,
                failure.toString()
            )
        }
        showBasicDialog(
            title = context.getString(R.string.main_error_title),
            message = message,
            isSuccess = false
        )
    }
}