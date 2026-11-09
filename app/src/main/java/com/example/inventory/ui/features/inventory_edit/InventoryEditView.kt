package com.example.inventory.ui.features.inventory_edit

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventory.R
import com.example.inventory.ui.features.inventory_edit.items.*
import com.example.inventory.ui.ui_kit.BottomSheetStates
import com.example.inventory.ui.ui_kit.LabelValueText
import com.example.inventory.ui.ui_kit.Photo
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.AddPhotoButton
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton
import com.example.inventory.ui.ui_kit.dialogs.BasicDialog
import com.example.inventory.ui.ui_kit.progress_indicator.AppCircularProgress
import kotlinx.coroutines.launch

@Composable
fun InventoryEditScreen(
    vm: InventoryEditViewModel,
    assetId: Long,
    mode: InventoryEditMode,
    onNavBack: () -> Unit,
    onSaved: () -> Unit,
    ) {
    val viewState by vm.viewState.collectAsStateWithLifecycle()
    val baseViewState by vm.baseViewState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val goBack: () -> Unit = {
        if (viewState.isSaving) {
            // Чекаємо завершення запису та прибирання файлів
        } else if (viewState.isSaved) {
            onSaved()
        } else if (!viewState.isMediaOperationRunning) {
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

    LaunchedEffect(assetId, mode) { vm.loadData(assetId, mode) }
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

    if (baseViewState.isShowAppBasicDialog && !viewState.isSaving) {
        BasicDialog(
            title = baseViewState.titleAppBasicDialog,
            message = baseViewState.messageAppBasicDialog,
            isSuccess = baseViewState.isSuccessAppBasicDialog,
            onClose = {
                vm.hideBasicDialog()
                when {
                    viewState.isSaved -> onSaved()
                    viewState.asset == null -> goBack()
                }
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
    if (viewState.isBottomSheetOpened) {
        BottomSheetStates(
            inventoryStates = viewState.inventoryStates,
            selectedInventoryState = viewState.inventoryStates
                .firstOrNull { it.code == viewState.selectedConditionCode },
            onSelectInventoryState = vm::onSelectInventoryState,
            onSaveInventoryState = vm::onSaveInventoryState,
            onCloseBottomSheet = vm::onCloseBottomSheet
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = { GetTopBar(viewState, goBack) },
        bottomBar = { GetBottomBar(vm.isSaveAvailable(), vm::onSave) }
    ) { padding ->
        if (!baseViewState.isDataLoaded) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                AppCircularProgress()
            }
        } else {
            GetContent(
                padding = padding,
                viewState = viewState,
                onNameChanged = vm::onNameChanged,
                onInventoryChanged = vm::onInventoryNumberChanged,
                onSerialChanged = vm::onSerialNumberChanged,
                onCountChanged = vm::onCountChanged,
                onCommentChanged = vm::onCommentChanged,
                onConditionClick = vm::onObjectStateClick,
                onAddPhoto = vm::showPhotoSource,
                onRemovePhoto = vm::removePhoto,
                evidenceRequired = vm.isEvidenceRequired()
            )
        }
    }
}

@Composable
private fun GetTopBar(state: InventoryEditState, onBack: () -> Unit) {
    val title = if (state.mode == InventoryEditMode.FULL) {
        stringResource(R.string.photo_edit_title)
    } else {
        state.asset?.name ?: stringResource(R.string.photo_edit_title)
    }
    AppTopBar(title = title, onBack = onBack)
}

@Composable
private fun GetContent(
    padding: PaddingValues,
    viewState: InventoryEditState,
    onNameChanged: (String) -> Unit,
    onInventoryChanged: (String) -> Unit,
    onSerialChanged: (String) -> Unit,
    onCountChanged: (String) -> Unit,
    onCommentChanged: (String) -> Unit,
    onConditionClick: () -> Unit,
    onAddPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    evidenceRequired: Boolean,
    ) {
    val asset = viewState.asset ?: return
    val enabled = !viewState.isMediaOperationRunning &&
            !viewState.isSaving && !viewState.isSaved
    val isFull = viewState.mode == InventoryEditMode.FULL
    Column(
        modifier = Modifier.fillMaxSize().padding(padding)
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isFull) {
            FormField(
                value = viewState.name,
                onValueChange = onNameChanged,
                label = stringResource(R.string.review_name),
                enabled = enabled,
                required = true,
                minLines = 2,
                counter = stringResource(
                    R.string.edit_name_counter, viewState.name.length
                )
            )
            FormField(
                value = viewState.inventoryNumber,
                onValueChange = onInventoryChanged,
                label = stringResource(R.string.review_inventory_number),
                enabled = enabled,
                required = true
            )
            FormField(
                value = viewState.serialNumber,
                onValueChange = onSerialChanged,
                label = stringResource(R.string.review_serial_number),
                enabled = enabled,
                required = true
            )
        } else {
            LabelValueText(
                label = stringResource(R.string.review_inventory_number),
                value = asset.invNumber
            )
        }
        FormField(
            value = viewState.count,
            onValueChange = onCountChanged,
            label = stringResource(R.string.review_quantity),
            enabled = enabled,
            required = true,
            keyboardType = KeyboardType.Number,
            error = if (viewState.count.isNotEmpty() &&
                (viewState.count.toIntOrNull() ?: 0) <= 0)
                stringResource(R.string.edit_invalid_quantity) else null
        )
        val conditionName = viewState.inventoryStates.firstOrNull {
            it.code == viewState.savedConditionCode
        }?.name.orEmpty()
        ConditionField(conditionName, enabled, onConditionClick)
        val uri = viewState.photoUri
        if (uri != null) {
            Photo(uri = uri, onDelete = onRemovePhoto, isEnabled = enabled)
        } else if (viewState.photoFileName != null) {
            Text(
                text = stringResource(R.string.edit_photo_unavailable),
                color = MaterialTheme.colorScheme.error
            )
            TextButton(onClick = onRemovePhoto, enabled = enabled) {
                Text(stringResource(R.string.photo_remove))
            }
        }
        AddPhotoButton(
            onClick = onAddPhoto,
            isRequiredField = evidenceRequired,
            isEnabled = enabled,
            hasPhoto = viewState.photoFileName != null
        )
        FormField(
            value = viewState.comment,
            onValueChange = onCommentChanged,
            label = stringResource(R.string.edit_comment),
            enabled = enabled,
            required = evidenceRequired,
            minLines = 3
        )
        LabelValueText(
            label = stringResource(R.string.review_location),
            value = asset.location
        )
        if (viewState.isMediaOperationRunning || viewState.isSaving) {
            Text(stringResource(if (viewState.isSaving)
                R.string.edit_saving else R.string.photo_working))
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    required: Boolean,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    counter: String? = null,
    error: String? = null,
    ) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        enabled = enabled,
        minLines = minLines,
        singleLine = minLines == 1,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = MaterialTheme.shapes.small,
        isError = error != null,
        supportingText = {
            Row(Modifier.fillMaxWidth()) {
                val hint = error ?: if (required)
                    stringResource(R.string.photo_required) else ""
                Text(hint, modifier = Modifier.weight(1f))
                if (counter != null) Text(counter)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConditionField(
    value: String,
    enabled: Boolean,
    onClick: () -> Unit,
    ) {
    ExposedDropdownMenuBox(
        expanded = false,
        onExpandedChange = { if (enabled) onClick() }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().menuAnchor(
                ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                enabled = enabled
            ),
            label = { Text(stringResource(R.string.review_condition)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
            },
            supportingText = {
                Text(stringResource(R.string.photo_required))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
private fun GetBottomBar(canSave: Boolean, onSave: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        PrimaryButton(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            title = stringResource(R.string.save),
            onClick = onSave,
            isEnabled = canSave
        )
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