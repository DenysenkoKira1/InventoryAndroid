package com.example.inventory.ui.features.inventory_edit

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventory.R
import com.example.inventory.ui.ui_kit.Photo
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.AddPhotoButton
import com.example.inventory.ui.ui_kit.dialogs.BasicDialog
import com.example.inventory.ui.ui_kit.progress_indicator.AppCircularProgress
import kotlinx.coroutines.launch

@Composable
fun InventoryEditScreen(
    vm: InventoryEditViewModel,
    assetId: Long,
    onNavBack: () -> Unit,
    ) {
    val viewState by vm.viewState.collectAsStateWithLifecycle()
    val baseViewState by vm.baseViewState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val goBack: () -> Unit = {
        if (!viewState.isMediaOperationRunning) {
            scope.launch {
                if (vm.discardPhotoChanges()) onNavBack()
            }
        }
    }

    val camera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(), vm::onCameraResult
    )
    val gallery = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(), vm::onGalleryResult
    )

    LaunchedEffect(assetId) { vm.loadData(assetId) }
    LaunchedEffect(viewState.isCameraLaunchRequested) {
        if (viewState.isCameraLaunchRequested) {
            val uri = viewState.cameraUri
            vm.cameraLaunchHandled()
            if (uri != null) {
                try {
                    camera.launch(uri)
                } catch (e: Exception) {
                    vm.onLaunchFailed(e, wasCamera = true)
                }
            }
        }
    }
    BackHandler(onBack = goBack)

    if (baseViewState.isShowAppBasicDialog) {
        BasicDialog(
            title = baseViewState.titleAppBasicDialog,
            message = baseViewState.messageAppBasicDialog,
            isSuccess = baseViewState.isSuccessAppBasicDialog,
            onClose = {
                vm.hideBasicDialog()
                if (viewState.asset == null) goBack()
            }
        )
    }
    if (viewState.isPhotoSourceDialogVisible) {
        PhotoSourceDialog(
            onCamera = vm::prepareCamera,
            onGallery = {
                if (vm.galleryStarted()) {
                    try {
                        val type = PickVisualMedia.ImageOnly
                        gallery.launch(PickVisualMediaRequest(type))
                    } catch (e: Exception) {
                        vm.onLaunchFailed(e, wasCamera = false)
                    }
                }
            },
            onCancel = vm::hidePhotoSource
        )
    }

    Scaffold(
        topBar = { AppTopBar(
            title = stringResource(R.string.photo_edit_title),
            onBack = goBack
        ) }
    ) { padding ->
        if (!baseViewState.isDataLoaded) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                AppCircularProgress()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val uri = viewState.photoUri
                if (uri != null) {
                    Photo(uri = uri, onDelete = vm::removePhoto,
                        isEnabled = !viewState.isMediaOperationRunning)
                }
                AddPhotoButton(
                    onClick = vm::showPhotoSource,
                    hasPhoto = viewState.photoFileName != null,
                    isEnabled = viewState.asset != null &&
                            !viewState.isMediaOperationRunning
                )
                if (viewState.isMediaOperationRunning) {
                    Text(stringResource(R.string.photo_working))
                }
            }
        }
    }
}

@Composable
private fun PhotoSourceDialog(
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onCancel: () -> Unit,
    ) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(stringResource(R.string.photo_add)) },
        text = { Column {
            TextButton(onClick = onCamera) {
                Text(stringResource(R.string.photo_camera))
            }
            TextButton(onClick = onGallery) {
                Text(stringResource(R.string.photo_gallery))
            }
        } },
        confirmButton = { TextButton(onClick = onCancel) {
            Text(stringResource(R.string.photo_cancel))
        } }
    )
}