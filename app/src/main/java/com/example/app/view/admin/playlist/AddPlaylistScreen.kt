package com.example.app.view.admin.playlist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlaylistPlay
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.viewmodel.PlaylistViewModel

@Composable
fun AddPlaylistScreen(
    playlistViewModel: PlaylistViewModel
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.tao_playlist),
            fontSize = 20.sp
        )
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
                    Icons.Default.PlaylistPlay, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(15.dp)
        )
        Spacer(modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it},
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
                if(title.isEmpty() || description.isEmpty()) {
                    val errorMessage = context.getString(R.string.thong_bao_khong_de_trong)
                    Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    playlistViewModel.createPlaylist(title,description)
                    val message = context.getString(R.string.tao_playlist_thanh_cong)
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                    title = ""
                    description = ""
                }
            }
        ) {
            Text(
                text = stringResource(R.string.xac_nhan),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}