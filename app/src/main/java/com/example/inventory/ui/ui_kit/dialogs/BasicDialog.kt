package com.example.inventory.ui.ui_kit.dialogs

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.example.inventory.R
import com.example.inventory.core.constants.DIALOG_DISPLAY_TIME_MS
import com.example.inventory.ui.ui_kit.buttons.AppIconButton
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDialog(
    modifier: Modifier = Modifier,
    isSuccess: Boolean,
    title: String? = null,
    message: String,
    onClose: () -> Unit,
    ) {

    LaunchedEffect(Unit) {
        delay(DIALOG_DISPLAY_TIME_MS)
        onClose()
    }

    BasicAlertDialog(
        modifier = modifier.padding(top = 56.dp),
        onDismissRequest = onClose,
        properties = DialogProperties()
    ) {
        (LocalView.current.parent as DialogWindowProvider).apply {
            window.setGravity(Gravity.TOP)
        }
        Box(
            modifier = Modifier
                .width(280.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    color = getBasicDialogMainColor(isSuccess)
                )
                .padding(start = 8.dp)
        ) {
            BasicDialogContent(
                title = title,
                text = message,
                isSuccess = isSuccess,
                onClose = onClose,
                )
        }
    }

}

@Composable
private fun BasicDialogContent(
    title: String? = null,
    text: String,
    isSuccess: Boolean,
    onClose: () -> Unit,
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSuccess) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                }
            )
    ) {

        Icon(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterVertically),
            tint = getBasicDialogMainColor(isSuccess),
            painter = if (isSuccess) {
                painterResource(R.drawable.ic_check_circle)
            } else {
                painterResource(R.drawable.ic_warning)
            },
            contentDescription = null,
            )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            if (!title.isNullOrEmpty()) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        AppIconButton(
            modifier = Modifier.align(Alignment.Top),
            onClick = onClose,
            iconRes = R.drawable.ic_cross,
            contentDescription = stringResource(R.string.dialog_close),
            )
    }
}

@Composable
private fun getBasicDialogMainColor(isSuccess: Boolean): Color {
    return if (isSuccess) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
}