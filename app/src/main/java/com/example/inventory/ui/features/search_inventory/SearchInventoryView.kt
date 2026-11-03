package com.example.inventory.ui.features.search_inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.inventory.R
import com.example.inventory.ui.ui_kit.InputTextField
import com.example.inventory.ui.ui_kit.bars.AppTopBar
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton

@Composable
fun SearchInventoryScreen(
//vm: SearchInventoryViewModel = hiltViewModel(),
//statusMode: StatusMode,
//onNavToInventoryInfoScreen:(MCard, StatusMode) -> Unit,
//onNavToNewInventoryScreen:() -> Unit,
//onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GetTopBar(
//                vm,
//                            onBack,
//                viewState
            )
        },
        bottomBar = {
            GetBottomBar(
//                vm,
//                viewState,
//                baseViewState,
//                onNavToNewInventoryScreen
            )
        }
    ) { innerPadding ->
        GetContent(

            innerPadding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GetTopBar(
//    vm: SearchInventoryViewModel,
//    onBack: () -> Unit,
//    viewState: SearchInventoryState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    AppTopBar(
        title = stringResource(R.string.search_by_serial_number),
//        onBack = onNavBack
    )
}

@Composable
private fun GetBottomBar () {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(color =MaterialTheme.colorScheme.background),
    ) {
        PrimaryButton(
            modifier = Modifier.padding(16.dp),
            title = stringResource(R.string.search),
            onClick = {},
            isEnabled = false
        )
    }
}

@Composable
private fun GetContent(
    innerPadding: PaddingValues
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = innerPadding)
            .background(color = MaterialTheme.colorScheme.surfaceDim),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            GetSearchInput()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) { }
    }
}

@Composable
private fun GetSearchInput(
//    vm: SearchInventoryViewModel,
//    viewState: SearchInventoryState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    InputTextField(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
//                modifier = Modifier.clickable { vm.onSearchClear() },
                painter = painterResource(id = R.drawable.ic_cross_circle),
                contentDescription = null
            )
        },
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
//                if (viewState.searchText.length >= MIN_SYMBOLS_TO_SEARCH) {
//                    vm.search()
//                }
            }
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
//        value = viewState.searchText,
//        onValueChange = vm::onSearchChanged
    )
}