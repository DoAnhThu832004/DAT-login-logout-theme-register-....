package com.example.app.view.Player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Comment
import com.example.app.view.Playlist.SelectArtistBottomSheet
import com.example.app.view.general.ConfirmDialog
import com.example.app.viewmodel.CommentViewModel

@Composable
fun CommentScreen(
    songId: String,
    commentViewModel: CommentViewModel
) {
    val commentState by commentViewModel.commentState
    val comments = commentState.comments ?: emptyList()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp) // Định nghĩa chiều cao cố định hoặc dùng weight
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bình luận",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                commentState.isLoading -> {
                    // Hiển thị vòng xoay tải dữ liệu
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF1DB954)
                    )
                }
                commentState.error != null -> {
                    Text(
                        text = commentState.error!!,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                commentState.comments != null && commentState.comments!!.isEmpty() -> {
                    // Chỉ hiển thị khi đã tải xong (comments không null) nhưng danh sách thực sự trống
                    Text(text = "Chưa có bình luận nào.", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cách viết an toàn: Truyền trực tiếp danh sách vào items
                        items(comments) { comment ->
                            DetailCommentScreen(
                                comment,
                                commentViewModel
                            )
                        }
                    }
                }
            }
        }
        CommentInputArea(songId,commentViewModel)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCommentScreen(
    comment: Comment,
    commentViewModel: CommentViewModel
) {
    val commentState by commentViewModel.commentState
    var showCommentSheet by remember { mutableStateOf(false) }
    val sheetStateComment = rememberModalBottomSheetState()
    Column(

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        color = MaterialTheme.colorScheme.onBackground
                    )
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = comment.username,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = comment.text,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = {
                    showCommentSheet = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Back"
                )
            }
        }
        Row(
            modifier = Modifier.padding(start = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {

                }
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Back"
                )
            }
            Text(
                text = stringResource(R.string.tra_loi),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
    if(showCommentSheet && commentState.comments != null) {
        ModalBottomSheet(
            onDismissRequest = { showCommentSheet = false },
            sheetState = sheetStateComment
        ) {
            SelectCommentBottomSheet(title = stringResource(R.string.binh_luan) + " " + comment.username, text = "Trả lời", delete = "Xoa", comment = comment,commentViewModel)
        }
    }
}
@Composable
fun CommentInputArea(
    songId: String,
    commentViewModel: CommentViewModel
) {
    var username by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(
                    color = MaterialTheme.colorScheme.onBackground
                )
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    text = "Viết bình luận...",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp)
        )
        IconButton(
            onClick = {
                commentViewModel.createComment(songId,username)
                username = ""
            },
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    color = MaterialTheme.colorScheme.onBackground
                ),
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIos,
                contentDescription = null,
                modifier = Modifier
                    .scale(scaleX = -1f, scaleY = 1f)
                    .padding(start = 8.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}