package com.example.inventory.ui.ui_kit.bars

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.inventory.R
import com.example.inventory.ui.ui_kit.buttons.AppIconButton

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {

    BoxWithConstraints() {
        TopAppBar(
            modifier = modifier,
            expandedHeight = 48.dp,
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = getTextStyleTitle(maxWidth)
                )
            },
            navigationIcon = {
                onBack?.also {
                    AppIconButton(onClick = it, iconRes = R.drawable.ic_back)
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )
    }

}

@Composable
private fun getTextStyleTitle(maxWidth: Dp): TextStyle {
   return when {
        maxWidth < 360.dp -> {
            MaterialTheme.typography.titleMedium
        }
        else -> {
            MaterialTheme.typography.titleLarge
        }
    }
}