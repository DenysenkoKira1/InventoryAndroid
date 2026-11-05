package com.example.inventory.ui.features.inventory_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventory.R
import com.example.inventory.core.constants.ONE_LINE_IN_TEXT
import com.example.inventory.ui.theme.InventoryTheme
import com.example.inventory.ui.theme.inventoriedColor
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton
import com.example.inventory.ui.ui_kit.buttons.SecondaryButton

@Composable
fun InventoryReviewScreen(
//    vm: InventoryInfoViewModel = hiltViewModel(),
//    position: MPosition,
//    card: MCard,
//    onCorrectClick: () -> Unit,
//    onIncorrectClick: () -> Unit,
//    onNavBack: () -> Unit,
//    onNavToEdit: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GetTopBar(
//                viewState = viewState,
//                onNavBack = onNavBack,
//                onNavToEdit = onNavToEdit
            )
        },
        bottomBar = {
            GetBottomBar()
        }
    ) { innerPadding ->
        GetContent(
            innerPadding
        )
    }
}

@Composable
private fun GetTopBar(
//    viewState: InventoryInfoState,
//    onNavBack: () -> Unit,
//    onNavToEdit: () -> Unit,
) {
    AppTopBar(
        title = stringResource(R.string.inventory)
    )
}
@Composable
private fun GetContent(
    innerPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = innerPadding)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = "hjfjgkjkjggfflkj987",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = ONE_LINE_IN_TEXT,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "asset.srnNumber",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = ONE_LINE_IN_TEXT,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "asset.name",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = ONE_LINE_IN_TEXT,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "asset.location",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = ONE_LINE_IN_TEXT,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "stringResource(R.string.inventoried)",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.inventoriedColor,
                maxLines = ONE_LINE_IN_TEXT,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@Composable
private fun GetBottomBar(
    modifier: Modifier = Modifier,
    onCorrectClick: () -> Unit = {},
    onIncorrectClick: () -> Unit = {},
    isDataLoaded: Boolean = true,
) {
    Card(
        modifier = modifier,
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

@Preview(name = "Світла тема", showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun LightPreview() {
    InventoryTheme(darkTheme = false) { InventoryReviewScreen() }
}

@Preview(name = "Темна тема", showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun DarkPreview() {
    InventoryTheme(darkTheme = true) { InventoryReviewScreen() }
}