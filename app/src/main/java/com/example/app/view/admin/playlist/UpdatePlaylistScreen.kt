package com.example.app.view.admin.playlist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.viewmodel.PlaylistViewModel

@Composable
fun UpdatePlaylistScreen(
    playlistId: String,
    playlistViewModel: PlaylistViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val playlistState by playlistViewModel.playlistState
    val playlists = playlistState.playlists ?: emptyList()
    val currentPlaylist = remember(playlists,playlistId) {
        playlists.find { it.id == playlistId }
    }
    var title by remember(currentPlaylist) {
        mutableStateOf(currentPlaylist?.title ?: "")
    }
    var description by remember(currentPlaylist) {
        mutableStateOf(currentPlaylist?.description ?: "")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null
                )
            }
            Text(
                text = stringResource(R.string.cap_nhap_playlist),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = {
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it},
            label = {
                Text(
                    text = stringResource(R.string.ten),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.MusicNote, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        OutlinedTextField(
            value = description,
            onValueChange = {description = it},
            label = {
                Text(
                    text = stringResource(R.string.mo_ta),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Description, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        Button(
            onClick = {
                playlistViewModel.updatePlaylist(playlistId,title,description)
                val message = context.getString(R.string.cap_nhap_bai_hat_thanh_cong)
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            Text(
                text = stringResource(R.string.xac_nhan),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}