package com.example.inventory.ui.features.search_inventory

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.inventory.R
import com.example.inventory.core.functional.eitherSuspend
import com.example.inventory.core.repository.IRepository
import com.example.inventory.ui.features.base.BaseViewModel
import com.example.inventory.ui.features.search_inventory.items.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchInventoryViewModel @Inject constructor(
    application: Application,
    private val repo: IRepository
    ) : BaseViewModel(application) {

    private val _viewState = MutableStateFlow(SearchInventoryState())
    val viewState = _viewState.asStateFlow()
    private var searchJob: Job? = null

    fun loadData() {
        if (!viewState.value.isOperationRunning) stopLoading()
    }

    fun search() {
        if (!viewState.value.canSearch) return
        if (searchJob?.isCompleted == false) return
        val serialNumber = viewState.value.searchText.trim()
        _viewState.update {
            it.copy(
                isOperationRunning = true,
                searchScreenState = SearchScreenState.ENTER_SERIAL_NUMBER
            )
        }
        startLoading()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.getAssetsRepo()
                    .findBySerial(serialNumber)
                    .eitherSuspend(
                        fnL = { showSearchError() },
                        fnR = { asset ->
                            if (asset == null) {
                                _viewState.update {
                                    it.copy(
                                        searchScreenState =
                                            SearchScreenState
                                                .NOT_FOUND_BY_SERIAL_NUMBER
                                    )
                                }
                            } else {
                                _viewState.update {
                                    it.copy(
                                        asset = asset,
                                        isGoToInventoryReviewScreen = true
                                    )
                                }
                            }
                        }
                    )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showSearchError()
            } finally {
                _viewState.update { it.copy(isOperationRunning = false) }
                stopLoading()
            }
        }
    }

    fun onSearchChanged(value: String) {
        if (viewState.value.isOperationRunning) return
        _viewState.update {
            it.copy(
                searchText = value,
                searchScreenState = SearchScreenState.ENTER_SERIAL_NUMBER
            )
        }
    }

    fun onSearchClear() {
        onSearchChanged("")
    }

    fun markNavigationHandled() {
        _viewState.update {
            it.copy(isGoToInventoryReviewScreen = false)
        }
    }

    fun onScreenClosed() {
        searchJob?.cancel()
        markNavigationHandled()
    }

    private fun showSearchError() {
        showBasicDialog(
            title = context.getString(R.string.search_error_title),
            message = context.getString(R.string.search_error_message),
            isSuccess = false
        )
    }
}