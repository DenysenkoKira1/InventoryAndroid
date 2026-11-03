package com.example.inventory.ui.features.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventory.R
import com.example.inventory.core.constants.ONE_LINE_IN_TEXT
import com.example.inventory.domain.models.Asset
import com.example.inventory.ui.theme.InventoryTheme
import com.example.inventory.ui.theme.inventoriedColor
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton
import com.example.inventory.ui.ui_kit.buttons.SecondaryButton


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val positionsState = rememberLazyListState()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GetAppTopBar()
        },
        bottomBar = {
            GetBottomBar()
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            val asset1 = Asset(
                invNumber = "inv-1",
                srnNumber = "ser-1",
                name = "name-1 kjejflksjfkekjflw jdsfkjiewo;aww ekjuuorjwksdj wodjakor",
                location = "location-1",
                state = true
            )
            val asset2 = Asset(
                invNumber = "inv-2",
                srnNumber = "ser-2",
                name = "name-2 kjejflksjfkekjflw jdsfkjiewo;aww ekjuuorjwksdj wodjakor",
                location = "location-2",
                state = false
            )
            val asset3 = Asset(
                invNumber = "inv-3",
                srnNumber = "ser-3",
                name = "name-3 kjejflksjfkekjflw jdsfkjiewo;aww ekjuuorjwksdj wodjakor",
                location = "location-3",
                state = true
            )
            val asset4 = Asset(
                invNumber = "inv-4",
                srnNumber = "ser-4",
                name = "name-4 kjejflksjfkekjflw jdsfkjiewo;aww ekjuuorjwksdj wodjakor",
                location = "location-4",
                state = true
            )
            val asset5 = Asset(
                invNumber = "inv-5",
                srnNumber = "ser-5",
                name = "name-5 kjejflksjfkekjflw jdsfkjiewo;aww ekjuuorjwksdj wodjakor",
                location = "location-5",
                state = true
            )
            val asset6 = Asset(
                invNumber = "inv-6",
                srnNumber = "ser-6",
                name = "name-6 kjejflksjfkekjflw jdsfkjiewo;aww ekjuuorjwksdj wodjakor",
                location = "location-6",
                state = false
            )
            val assets = listOf(asset1, asset2, asset3, asset4, asset5, asset6)
            val uncheckedAssets = assets.filter { !it.state }
            val checkedAssets = assets.filter { it.state }

            val shownAssets = if (selectedTabIndex == 0) {
                uncheckedAssets
            } else {
                checkedAssets
            }
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            "${stringResource(R.string.unverified)} ${uncheckedAssets.size}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            "${stringResource(R.string.verified)} ${checkedAssets.size}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
            GetContent(
                assets = shownAssets.toMutableList(),
                positionsState = positionsState
            )
        }
    }
}

@Composable
private fun GetAppTopBar(
) {
     AppTopBar(
        title = stringResource(R.string.inventory),
        actions = {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    painterResource(R.drawable.clear_eraser),
                    contentDescription = null
                )
            }
        }
    )
}


@Composable
private fun GetBottomBar(
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(color =MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PrimaryButton(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            title = stringResource(R.string.scan),
            onClick = {}
        )
        SecondaryButton(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            title = stringResource(R.string.search_inv),
            onClick = {}
        )
    }
}

@Composable
private fun GetContent(
//    vm: MainViewModel,
    assets: MutableList<Asset>,
    positionsState: LazyListState,
) {
    if (assets.isEmpty()) {
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceDim),
        state = positionsState,
    ) {
        items(assets) { asset ->
           ContentCard(asset)
        }
    }
}

@Composable
private fun ContentCard(
//    vm: MainViewModel,
    asset: Asset
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable(
                onClick = {
//                    vm.onCardClick(position)
                }
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
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
                Text(
                    text = asset.invNumber,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = ONE_LINE_IN_TEXT,
                    overflow = TextOverflow.Ellipsis
                )

                if (asset.state) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = asset.srnNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = ONE_LINE_IN_TEXT,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = ONE_LINE_IN_TEXT,
                    overflow = TextOverflow.Ellipsis
                )

                if (asset.state) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = asset.location,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = ONE_LINE_IN_TEXT,
                        overflow = TextOverflow.Ellipsis
                    )
                }

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
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                tint = MaterialTheme.colorScheme.tertiary,
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null
            )
        }
    }
}

@Preview(name = "Світла тема", showBackground = true,
    widthDp = 390, heightDp = 760)
@Composable
private fun LightPreview() {
    InventoryTheme(darkTheme = false) { MainScreen() }
}

@Preview(name = "Темна тема", showBackground = true,
    widthDp = 390, heightDp = 760)
@Composable
private fun DarkPreview() {
    InventoryTheme(darkTheme = true) { MainScreen() }
}
