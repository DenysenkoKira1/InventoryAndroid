package com.example.inventory.ui.ui_kit.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.inventory.R
import com.example.inventory.ui.ui_kit.buttons.PrimaryButton
import com.example.inventory.ui.ui_kit.buttons.SecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmActionDialog(
    title: String,
    message: String,
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    ) {
    BasicAlertDialog(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        onDismissRequest = onClose,
        properties = DialogProperties()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Image(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = null,
                    )
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                SecondaryButton(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.no),
                    onClick = onClose
                )
                PrimaryButton(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.yes),
                    onClick = onConfirm
                )
            }
        }
    }
}