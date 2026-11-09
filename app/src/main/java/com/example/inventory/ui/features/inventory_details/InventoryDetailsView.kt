package com.example.inventory.ui.features.inventory_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventory.R
import com.example.inventory.ui.features.inventory_details.items.*
import com.example.inventory.ui.ui_kit.LabelValueText
import com.example.inventory.ui.ui_kit.Photo
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.AppIconButton
import com.example.inventory.ui.ui_kit.dialogs.BasicDialog
import com.example.inventory.ui.ui_kit.progress_indicator.*

@Composable
fun InventoryDetailsScreen(
    vm: InventoryDetailsViewModel,
    assetId: Long,
    onNavBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    ) {
    val viewState by vm.viewState.collectAsStateWithLifecycle()
    val baseViewState by vm.baseViewState.collectAsStateWithLifecycle()

    LaunchedEffect(assetId) { vm.loadData(assetId) }

    if (baseViewState.isShowAppBasicDialog) {
        BasicDialog(
            title = baseViewState.titleAppBasicDialog,
            message = baseViewState.messageAppBasicDialog,
            isSuccess = baseViewState.isSuccessAppBasicDialog,
            onClose = {
                vm.hideBasicDialog()
                if (viewState.asset == null) onNavBack()
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GetTopBar(
                viewState = viewState,
                canEdit = baseViewState.isDataLoaded &&
                        viewState.asset?.state == true,
                onNavBack = onNavBack,
                onEditClick = onEditClick
            )
        }
    ) { padding ->
        if (!baseViewState.isDataLoaded) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                AppCircularProgress()
            }
        } else {
            GetContent(padding, viewState)
        }
    }
}

@Composable
private fun GetTopBar(
    viewState: InventoryDetailsState,
    canEdit: Boolean,
    onNavBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    ) {
    AppTopBar(
        title = viewState.asset?.name
            ?: stringResource(R.string.inventory),
        onBack = onNavBack,
        actions = {
            if (canEdit) {
                AppIconButton(
                    onClick = {
                        viewState.asset?.let { onEditClick(it.id) }
                    },
                    iconRes = R.drawable.ic_edit,
                    contentDescription =
                        stringResource(R.string.details_edit),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun GetContent(
    padding: PaddingValues,
    viewState: InventoryDetailsState,
    ) {
    val asset = viewState.asset ?: return
    Column(
        modifier = Modifier.fillMaxSize().padding(padding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                LabelValueText(
                    label = stringResource(R.string.review_name),
                    value = asset.name
                )
                LabelValueText(
                    label = stringResource(
                        R.string.review_inventory_number
                    ),
                    value = asset.invNumber
                )
                LabelValueText(
                    label = stringResource(R.string.review_serial_number),
                    value = asset.srnNumber
                )
                LabelValueText(
                    label = stringResource(R.string.review_condition),
                    value = viewState.conditionName
                )
                LabelValueText(
                    label = stringResource(R.string.review_quantity),
                    value = stringResource(
                        R.string.review_quantity_value,
                        asset.quantity, asset.unit
                    )
                )
                LabelValueText(
                    label = stringResource(R.string.review_location),
                    value = asset.location
                )
                LabelValueText(
                    label = stringResource(R.string.edit_comment),
                    value = asset.comment.ifBlank {
                        stringResource(R.string.details_no_comment)
                    }
                )
                val uri = viewState.photoUri
                when {
                    uri != null -> Photo(uri = uri, onDelete = null)
                    asset.photoFileName != null -> Text(
                        text = stringResource(
                            R.string.details_photo_unavailable
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                    else -> Text(stringResource(R.string.details_no_photo))
                }
            }
        }
    }
}