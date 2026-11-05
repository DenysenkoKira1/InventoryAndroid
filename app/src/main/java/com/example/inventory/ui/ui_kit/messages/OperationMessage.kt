package com.example.inventory.ui.ui_kit.messages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.inventory.R

@Composable
fun OperationMessage(
    isSuccess: Boolean,
    message: String,
    onClose: () -> Unit,
    title: String = if (isSuccess) "Успішно" else "Помилка"
) {
    val colors = MaterialTheme.colorScheme
    val container = if (isSuccess) {
        colors.primaryContainer
    } else {
        colors.errorContainer
    }
    val content = if (isSuccess) {
        colors.onPrimaryContainer
    } else {
        colors.onErrorContainer
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 48.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier.padding(horizontal = 16.dp)
                    .widthIn(max = 360.dp).fillMaxWidth(),
                color = container,
                contentColor = content,
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(top = 4.dp)
                            .size(24.dp)
                            .border(2.dp, content, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSuccess) "✓" else "!",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(title, style = MaterialTheme.typography.titleSmall)
                        Text(message, style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cross_circle),
                            contentDescription = "Закрити повідомлення"
                        )
                    }
                }
            }
        }
    }
}