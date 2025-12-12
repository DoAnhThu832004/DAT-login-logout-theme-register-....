package com.example.app.view.general

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ConfirmDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    icon : ImageVector? = null,
    iconColor : Color? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = showDialog,
        enter = fadeIn() + scaleIn(initialScale = 1f),
        exit = fadeOut() + scaleOut(targetScale = 1f)
    ) {
        if(showDialog) {
            AlertDialog(
                onDismissRequest = { onDismiss() },
                icon = {
                    if (icon != null) {
                        if (iconColor != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor ?: MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                title = { Text(title) },
                text = { Text(text = message) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onConfirm()
                        }
                    ) {
                        Text(confirmText)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(dismissText)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                textContentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}