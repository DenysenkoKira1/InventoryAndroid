package com.example.inventory.ui.features.base

data class BaseState(
    val isDataLoaded: Boolean = false,
    val isShowAppBasicDialog: Boolean = false,
    val titleAppBasicDialog: String = "",
    val messageAppBasicDialog: String = "",
    val isSuccessAppBasicDialog: Boolean = false,
    val isShowAppConfirmActionDialog: Boolean = false,
    val titleAppConfirmActionDialog: String = "",
    val messageAppConfirmActionDialog: String = "",
    val onConfirmAppConfirmActionDialog: (() -> Unit)? = null,
    )