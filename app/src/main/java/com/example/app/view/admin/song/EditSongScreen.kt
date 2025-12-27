package com.example.app.view.admin.song

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
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
import com.example.app.model.response.Song
import com.example.app.viewmodel.SongViewModel

@Composable
fun EditSongScreen(
    songViewModel: SongViewModel,
    songToEdit: Song,
    songId : String
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var nameSong by remember(songToEdit) {
            mutableStateOf(songToEdit?.name ?: "")
        }
        var description by remember(songToEdit) { mutableStateOf(songToEdit?.description ?: "") }
        var duration by remember(songToEdit) { mutableStateOf(songToEdit?.duration.toString() ?: "") }
        var releasedDate by remember(songToEdit) { mutableStateOf(songToEdit?.releasedDate ?: "") }
        var status by remember(songToEdit) { mutableStateOf(songToEdit?.status ?: "") }
        var type by remember(songToEdit) { mutableStateOf(songToEdit?.type ?: "") }
        Text(
            text = stringResource(R.string.chinh_sua_bai_hat),
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = nameSong,
            onValueChange = { nameSong = it},
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
        Spacer(modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = status,
            onValueChange = { status = it},
            label = {
                Text(
                    text = stringResource(R.string.trang_thai),
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
        Spacer(modifier = Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it},
            label = {
                Text(
                    text = stringResource(R.string.thoi_gian),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Timer, contentDescription = null,
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
            value = releasedDate,
            onValueChange = { releasedDate = it},
            label = {
                Text(
                    text = stringResource(R.string.ngay_phat_hanh),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange, contentDescription = null,
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
            value = type,
            onValueChange = { type = it},
            label = {
                Text(
                    text = stringResource(R.string.ngay_phat_hanh),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.DateRange, contentDescription = null,
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

        Button(
            onClick = {
                if(nameSong.isEmpty() || description.isEmpty() || duration.isEmpty() || releasedDate.isEmpty() || type.isEmpty() || status.isEmpty()) {
                    val errorMessage = context.getString(R.string.thong_bao_khong_de_trong)
                    Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    songViewModel.updateSong(songId, nameSong, description,status, duration.toInt(), releasedDate, type)
                    val successMessage = context.getString(R.string.cap_nhap_bai_hat_thanh_cong)
                    Toast.makeText(
                        context,
                        successMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    nameSong = ""
                    description = ""
                    duration = ""
                    releasedDate = ""
                    type = ""
                    status = ""
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