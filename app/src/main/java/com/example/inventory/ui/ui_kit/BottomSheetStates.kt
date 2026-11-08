package com.example.inventory.ui.ui_kit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.inventory.R
import com.example.inventory.domain.models.AssetCondition
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton
import com.example.inventory.ui.ui_kit.buttons.SecondaryButton

@Composable
fun BottomSheetStates(
    inventoryStates: List<AssetCondition>,
    selectedInventoryState: AssetCondition?,
    onSelectInventoryState: (AssetCondition) -> Unit,
    onSaveInventoryState: () -> Unit,
    onCloseBottomSheet: () -> Unit,
    ) {
    val canSave = inventoryStates.any {
        it.code == selectedInventoryState?.code
    }

    BottomSheet(
        label = stringResource(R.string.review_condition),
        onDismissRequest = onCloseBottomSheet
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            GetContent(
                modifier = Modifier.weight(1f),
                inventoryStates = inventoryStates,
                selectedInventoryState = selectedInventoryState,
                onSelectInventoryState = onSelectInventoryState
            )
            GetBottomBar(
                canSave = canSave,
                onSave = onSaveInventoryState,
                onCancel = onCloseBottomSheet
            )
        }
    }
}

@Composable
private fun GetContent(
    modifier: Modifier,
    inventoryStates: List<AssetCondition>,
    selectedInventoryState: AssetCondition?,
    onSelectInventoryState: (AssetCondition) -> Unit,
    ) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (inventoryStates.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.condition_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        items(inventoryStates, key = { it.code }) { state ->
            InventoryStateCard(
                text = state.name,
                isSelected = state.code ==
                        selectedInventoryState?.code,
                onClick = { onSelectInventoryState(state) }
            )
        }
    }
}

@Composable
private fun GetBottomBar(
    canSave: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    ) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SecondaryButton(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.cancel),
            onClick = onCancel
        )
        PrimaryButton(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.save),
            onClick = onSave,
            isEnabled = canSave
        )
    }
}