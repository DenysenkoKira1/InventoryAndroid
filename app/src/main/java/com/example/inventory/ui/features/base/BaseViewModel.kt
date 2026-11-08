package com.example.inventory.ui.features.base

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class BaseViewModel(
    application: Application,
    ) : AndroidViewModel(application) {

    val context: Context
        get() = getApplication<Application>().applicationContext
    val configuration: Configuration
        get() = getApplication<Application>().resources.configuration
    val displayMetrics: DisplayMetrics
        get() = getApplication<Application>().resources.displayMetrics
    val contentResolver: ContentResolver
        get() = getApplication<Application>().contentResolver


    protected val _baseViewState: MutableStateFlow<BaseState> =
        MutableStateFlow(BaseState())
    val baseViewState = _baseViewState.asStateFlow()

    fun showBasicDialog(
        message: String,
        title: String = "",
        isSuccess: Boolean,
        ) {
        _baseViewState.update {
            it.copy(
                isShowAppBasicDialog = true,
                messageAppBasicDialog = message,
                titleAppBasicDialog = title,
                isSuccessAppBasicDialog = isSuccess,
                )
        }
    }

    fun hideBasicDialog() {
        _baseViewState.update {
            it.copy(
                isShowAppBasicDialog = false,
                )
        }
    }

    fun showConfirmActionDialog(
        message: String,
        title: String = "",
        onConfirm: () -> Unit,
        ) {
        _baseViewState.update {
            it.copy(
                isShowAppConfirmActionDialog = true,
                messageAppConfirmActionDialog = message,
                titleAppConfirmActionDialog = title,
                onConfirmAppConfirmActionDialog = onConfirm
            )
        }
    }

    fun hideConfirmActionDialog() {
        _baseViewState.update {
            it.copy(
                isShowAppConfirmActionDialog = false,
                onConfirmAppConfirmActionDialog = null
            )
        }
    }

    fun startLoading() {
        _baseViewState.update {
            it.copy(
                isDataLoaded = false,
                )
        }
    }

    fun stopLoading() {
        _baseViewState.update {
            it.copy(
                isDataLoaded = true,
                )
        }

    }

    fun checkingExceptionOnNull(e: Throwable): String {
        return if (e.message.isNullOrEmpty()) {
            e.toString()
        } else {
            e.message.orEmpty()
        }
    }

}