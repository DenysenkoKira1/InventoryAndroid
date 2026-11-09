package com.example.inventory.ui.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventory.R
import com.example.inventory.core.constants.ONE_LINE_IN_TEXT
import com.example.inventory.domain.models.Asset
import com.example.inventory.ui.theme.inventoriedColor
import com.example.inventory.ui.ui_kit.dialogs.BasicDialog
import com.example.inventory.ui.ui_kit.dialogs.ConfirmActionDialog
import com.example.inventory.ui.ui_kit.progress_indicator.AppCircularProgress
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton
import com.example.inventory.ui.ui_kit.buttons.SecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    vm: MainViewModel,
    onNavToSearchInventoryScreen: () -> Unit,
    onNavToInventoryReviewScreen: (Long) -> Unit,
    onNavToInventoryDetailsScreen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    ) {
    val viewState by vm.viewState.collectAsStateWithLifecycle()
    val baseViewState by vm.baseViewState.collectAsStateWithLifecycle()
    val positionsState = rememberLazyListState()
    val isEnabled = baseViewState.isDataLoaded &&
            !viewState.isOperationRunning

    LaunchedEffect(Unit) { vm.loadData() }

    if (baseViewState.isShowAppBasicDialog) {
        BasicDialog(
            isSuccess = baseViewState.isSuccessAppBasicDialog,
            title = baseViewState.titleAppBasicDialog,
            message = baseViewState.messageAppBasicDialog,
            onClose = vm::hideBasicDialog
        )
    }
    if (baseViewState.isShowAppConfirmActionDialog) {
        baseViewState.onConfirmAppConfirmActionDialog?.also { confirm ->
            ConfirmActionDialog(
                title = baseViewState.titleAppConfirmActionDialog,
                message = baseViewState.messageAppConfirmActionDialog,
                onConfirm = confirm,
                onClose = vm::hideConfirmActionDialog
            )
        }
    }
    LaunchedEffect(
        viewState.isGoToInventoryReviewScreen
    ) {
        if (viewState.isGoToInventoryReviewScreen) {
            val asset = viewState.selectedAsset
            vm.markNavigationHandled()
            if (asset.state) {
                onNavToInventoryDetailsScreen(asset.id)
            } else {
                onNavToInventoryReviewScreen(asset.id)
            }
        }
    }

    val uncheckedAssets = viewState.uncheckedAssets
    val checkedAssets = viewState.checkedAssets
    val shownAssets = if (viewState.selectedTabIndex == 0) {
        uncheckedAssets
    } else {
        checkedAssets
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { GetAppTopBar(vm, isEnabled) },
        bottomBar = {
            GetBottomBar(
                onSearch = onNavToSearchInventoryScreen,
                onScan = vm::scan,
                isEnabled = isEnabled
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = viewState.selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = viewState.selectedTabIndex == 0,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { vm.onTabSelected(0) },
                    text = {
                        Text(
                            "${stringResource(R.string.unverified)} ${uncheckedAssets.size}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
                Tab(
                    selected = viewState.selectedTabIndex == 1,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { vm.onTabSelected(1) },
                    text = {
                        Text(
                            "${stringResource(R.string.verified)} ${checkedAssets.size}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
            if (!isEnabled) {
                AppCircularProgress()
                return@Scaffold
            }
            GetContent(vm, shownAssets, positionsState)
        }
    }

}

@Composable
private fun GetAppTopBar(
    vm: MainViewModel,
    isEnabled: Boolean
) {
    AppTopBar(
        title = stringResource(R.string.inventory),
        actions = {
            IconButton(
                onClick = vm::onClickClear,
                enabled = isEnabled
            ) {
                Icon(
                    painterResource(R.drawable.clear_eraser),
                    contentDescription =
                        stringResource(R.string.main_clear_title)
                )
            }
        }
    )
}

@Composable
private fun GetBottomBar(
    onSearch: () -> Unit,
    onScan: () -> Unit,
    isEnabled: Boolean
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PrimaryButton(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            title = stringResource(R.string.scan),
            onClick = onScan,
            isEnabled = isEnabled
        )
        SecondaryButton(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            title = stringResource(R.string.search_inv),
            onClick = onSearch,
            isEnabled = isEnabled
        )
    }
}

@Composable
private fun GetContent(
    vm: MainViewModel,
    assets: List<Asset>,
    positionsState: LazyListState,
    ) {
    if (assets.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.main_empty_list),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        state = positionsState,
        ) {
        items(assets, key = { it.id }) { asset ->
            ContentCard(vm, asset)
        }
    }
}

@Composable
private fun ContentCard(
    vm: MainViewModel,
    asset: Asset
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable(
                enabled = asset.state,
                onClick = { vm.onCardClick(asset) }
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                if (asset.state) {
                    Text(
                        text = asset.invNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = ONE_LINE_IN_TEXT,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (asset.state) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = asset.srnNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = ONE_LINE_IN_TEXT,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = ONE_LINE_IN_TEXT,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = asset.location,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = ONE_LINE_IN_TEXT,
                    overflow = TextOverflow.Ellipsis
                )

                if (asset.state) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.inventoried),
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.inventoriedColor,
                        maxLines = ONE_LINE_IN_TEXT,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (asset.state) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
        }
    }
}