package com.example.inventory.ui.ui_kit.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.inventory.R
import com.example.inventory.ui.theme.InventoryTheme

@Composable
fun AddPhotoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRequiredField: Boolean = false,
    isEnabled: Boolean = true,
    hasPhoto: Boolean = false,
    ) {
    val borderColor = if (isEnabled) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    Column(
        modifier = modifier.fillMaxWidth().drawBehind {
            drawRoundRect(
                color = borderColor,
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = Stroke(width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(6.dp.toPx(), 4.dp.toPx())))
            )
        }.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(onClick = onClick, enabled = isEnabled) {
            Icon(
                painterResource(R.drawable.ic_camera),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(if (hasPhoto) R.string.photo_replace
                else R.string.photo_add)
            )
        }
        if (isRequiredField && !hasPhoto) {
            Text(
                text = stringResource(R.string.photo_required),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPhotoButtonPreview() {
    InventoryTheme { AddPhotoButton(onClick = {}) }
}