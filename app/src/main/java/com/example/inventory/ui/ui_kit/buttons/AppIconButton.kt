package com.example.inventory.ui.ui_kit.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun AppIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    @DrawableRes iconRes: Int,
    //tint: Color = AppTheme.colorScheme.tertiary
) {

//    val interactionSource = remember { MutableInteractionSource() }
//    val isPressed by interactionSource.collectIsPressedAsState()
//
//    val containerColor = when {
//        //isPressed -> AppTheme.colorScheme.accentPressedContainer
//        else -> Color.Transparent
//    }
//
//    val contentColor = when {
////        isPressed -> AppTheme.colorScheme.accentPressed
////        else -> AppTheme.colorScheme.tertiary
//    }
//IconButton(
////        modifier = modifier,
////        onClick = onClick,
////        enabled = isEnabled,
////        interactionSource = interactionSource,
////        colors = IconButtonDefaults.iconButtonColors(
////            contentColor = contentColor,
////            disabledContentColor = AppTheme.colorScheme.secondary,
////            containerColor = containerColor,
////            disabledContainerColor = Color.Transparent
////        )
////    ) {
////        Icon(
////            tint = tint,
////            painter = painterResource(id = iconRes),
////            contentDescription = null
////        )
////    }
//
}