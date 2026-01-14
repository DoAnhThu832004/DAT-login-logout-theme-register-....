package com.example.app.view.admin.playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Playlist
import com.example.app.view.general.ConfirmDialog

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onDeleteClick: (String) -> Unit
) {
    var show by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(playlist.imageUrlP.isNullOrEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                    )
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(playlist.imageUrlP),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                )
            }
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {
                    show = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    }
    ConfirmDialog(
        showDialog = show,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_xoa_bai_hat),
        confirmText = stringResource(R.string.xac_nhan),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            show = false
            onDeleteClick(playlist.id)
        },
        onDismiss = {
            show = false
        }
    )
}