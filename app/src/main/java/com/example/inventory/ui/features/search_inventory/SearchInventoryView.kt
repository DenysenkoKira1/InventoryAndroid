package com.example.inventory.ui.features.search_inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventory.R
import com.example.inventory.ui.features.search_inventory.items.*
import com.example.inventory.ui.ui_kit.InputTextField
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.AppIconButton
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton
import com.example.inventory.ui.ui_kit.dialogs.BasicDialog
import com.example.inventory.ui.ui_kit.progress_indicator.*

@Composable
fun SearchInventoryScreen(
    vm: SearchInventoryViewModel,
    onBack: () -> Unit,
    onNavToInventoryReviewScreen: (Long) -> Unit,
    onNavToInventoryDetailsScreen: (Long) -> Unit,
    ) {
    val viewState by vm.viewState.collectAsStateWithLifecycle()
    val baseViewState by vm.baseViewState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) { vm.loadData() }
    DisposableEffect(vm) {
        onDispose { vm.onScreenClosed() }
    }
    LaunchedEffect(viewState.isGoToInventoryReviewScreen) {
        if (viewState.isGoToInventoryReviewScreen) {
            val asset = viewState.asset
            vm.markNavigationHandled()
            if (asset.state) {
                onNavToInventoryDetailsScreen(asset.id)
            } else {
                onNavToInventoryReviewScreen(asset.id)
            }
        }
    }

    if (baseViewState.isShowAppBasicDialog) {
        BasicDialog(
            title = baseViewState.titleAppBasicDialog,
            message = baseViewState.messageAppBasicDialog,
            isSuccess = baseViewState.isSuccessAppBasicDialog,
            onClose = vm::hideBasicDialog
        )
    }

    val search: () -> Unit = {
        keyboardController?.hide()
        vm.search()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GetTopBar(onBack = {
                keyboardController?.hide()
                onBack()
            })
        },
        bottomBar = {
            GetBottomBar(
                onSearch = search,
                isEnabled = baseViewState.isDataLoaded
                        && viewState.canSearch
            )
        }
    ) { innerPadding ->
        GetContent(
            innerPadding = innerPadding,
            viewState = viewState,
            isDataLoaded = baseViewState.isDataLoaded,
            onSearchChanged = vm::onSearchChanged,
            onSearchClear = vm::onSearchClear,
            onSearch = search
        )
    }
}

@Composable
private fun GetTopBar(onBack: () -> Unit) {
    AppTopBar(
        title = stringResource(R.string.search_by_serial_number),
        onBack = onBack
    )
}

@Composable
private fun GetBottomBar(
    onSearch: () -> Unit,
    isEnabled: Boolean,
    ) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        PrimaryButton(
            modifier = Modifier.padding(16.dp),
            title = stringResource(R.string.search),
            onClick = onSearch,
            isEnabled = isEnabled
        )
    }
}

@Composable
private fun GetContent(
    innerPadding: PaddingValues,
    viewState: SearchInventoryState,
    isDataLoaded: Boolean,
    onSearchChanged: (String) -> Unit,
    onSearchClear: () -> Unit,
    onSearch: () -> Unit,
    ) {
    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            GetSearchInput(
                viewState, onSearchChanged, onSearchClear, onSearch
            )
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            if (!isDataLoaded) {
                AppCircularProgress()
            } else if (viewState.searchScreenState ==
                SearchScreenState.NOT_FOUND_BY_SERIAL_NUMBER) {
                Text(
                    text = stringResource(R.string.search_not_found),
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun GetSearchInput(
    viewState: SearchInventoryState,
    onSearchChanged: (String) -> Unit,
    onSearchClear: () -> Unit,
    onSearch: () -> Unit,
    ) {
    InputTextField(
        modifier = Modifier.padding(
            start = 16.dp, end = 16.dp, bottom = 8.dp
        ),
        value = viewState.searchText,
        onValueChange = onSearchChanged,
        label = stringResource(R.string.search_serial_label),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null
            )
        },
        trailingIcon = {
            AppIconButton(
                onClick = onSearchClear,
                iconRes = R.drawable.ic_cross_circle,
                contentDescription = stringResource(
                    R.string.search_clear
                ),
                isEnabled = !viewState.isOperationRunning
            )
        },
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}