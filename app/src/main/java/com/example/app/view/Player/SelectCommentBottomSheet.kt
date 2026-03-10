package com.example.app.view.Player

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.app.R
import com.example.app.model.response.Comment
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.CommentViewModel
import com.example.app.viewmodel.PlaylistViewModel

@Composable
fun SelectCommentBottomSheet(
    title: String,
    text: String = "",
    delete: String = "",
    comment: Comment,
    commentViewModel: CommentViewModel
) {
    val context = LocalContext.current
    var show by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        )
        if(comment.owner) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clickable {  },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ChatBubble, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clickable { show = true }
            ) {
                Icon(
                    Icons.Default.Delete, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = delete,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ChatBubble, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(
                    Icons.Default.Warning, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "Báo cáo",
                    color = MaterialTheme.colorScheme.onPrimary
                )
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
                commentViewModel.deleteComment(comment.id)

            },
            onDismiss = {
                show = false
            }
        )
    }
}