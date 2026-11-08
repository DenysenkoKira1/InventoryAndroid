package com.example.inventory.ui.features.inventory_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventory.R
import com.example.inventory.domain.models.Asset
import com.example.inventory.ui.features.inventory_info.items.*
import com.example.inventory.ui.ui_kit.LabelValueText
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton
import com.example.inventory.ui.ui_kit.buttons.SecondaryButton
import com.example.inventory.ui.ui_kit.dialogs.BasicDialog
import com.example.inventory.ui.ui_kit.progress_indicator.*

@Composable
fun InventoryReviewScreen(
    vm: InventoryReviewViewModel,
    assetId: Long,
    onNavBack: () -> Unit,
    onCorrectClick: (Long) -> Unit,
    onIncorrectClick: (Long) -> Unit,
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
        topBar = { GetTopBar(onNavBack) },
        bottomBar = {
            GetBottomBar(
                onCorrectClick = {
                    viewState.asset?.let { onCorrectClick(it.id) }
                },
                onIncorrectClick = {
                    viewState.asset?.let { onIncorrectClick(it.id) }
                },
                isDataLoaded = baseViewState.isDataLoaded
                        && viewState.asset != null
            )
        }
    ) { innerPadding ->
        if (!baseViewState.isDataLoaded) {
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                AppCircularProgress()
            }
        } else {
            GetContent(innerPadding, viewState)
        }
    }
}

@Composable
private fun GetTopBar(onNavBack: () -> Unit) {
    AppTopBar(
        title = stringResource(R.string.inventory),
        onBack = onNavBack
    )
}

@Composable
private fun GetContent(
    innerPadding: PaddingValues,
    viewState: InventoryReviewState,
    ) {
    val asset: Asset = viewState.asset ?: return
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
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
            }
        }
    }
}

@Composable
private fun GetBottomBar(
    onCorrectClick: () -> Unit,
    onIncorrectClick: () -> Unit,
    isDataLoaded: Boolean,
    ) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecondaryButton(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.incorrect),
                onClick = onIncorrectClick,
                isEnabled = isDataLoaded
            )
            PrimaryButton(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.correct),
                onClick = onCorrectClick,
                isEnabled = isDataLoaded
            )
        }
    }
}