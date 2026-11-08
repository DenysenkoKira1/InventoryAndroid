package com.example.inventory.ui.ui_kit

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.inventory.R
import com.example.inventory.ui.ui_kit.buttons.AppIconButton

@Composable
fun Photo(
    uri: Uri,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    isEnabled: Boolean = true,
    ) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Box {
            SubcomposeAsyncImage(
                model = uri,
                contentDescription =
                    stringResource(R.string.photo_description),
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop,
                loading = { Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                } },
                error = { Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.photo_preview_error),
                    color = MaterialTheme.colorScheme.error
                ) }
            )
            if (onDelete != null) {
                AppIconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = onDelete,
                    iconRes = R.drawable.ic_cross,
                    contentDescription =
                        stringResource(R.string.photo_remove),
                    isEnabled = isEnabled
                )
            }
        }
    }
}