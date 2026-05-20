package com.example.app.view.admin.song

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.app.view.general.DateDialog
import com.example.app.viewmodel.SongViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.mutableStateListOf
@Composable
fun AddSong(
    songViewModel: SongViewModel
) {
    val songState by songViewModel.songState.collectAsState()
    LaunchedEffect(Unit) {
        songViewModel.getGenres()
    }
    val selectedGenres = remember { mutableStateListOf<String>() }
    var showGenreDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var shouldAnimate by rememberSaveable { mutableStateOf(true) }
    var nameSong by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var releasedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    if (showDatePicker) {
        DateDialog(
            onDateSelected = { formattedDate ->
                releasedDate = formattedDate
            },
            onDismiss = { showDatePicker = false }
        )
    }
    BoxWithConstraints {
        val startOffset = maxWidth
        val alphaAnim = remember { androidx.compose.animation.core.Animatable(0f) }
        val slideAnim = remember { androidx.compose.animation.core.Animatable(startOffset.value) }
        LaunchedEffect(key1 = shouldAnimate) {
            launch {
                alphaAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400)
                )
            }
            launch {
                slideAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy, // Độ nảy vừa phải
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .offset(x = slideAnim.value.dp)
                .alpha(alphaAnim.value)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.tao_bai_hat),
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
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = stringResource(R.string.ngay_phat_hanh),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    leadingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(15.dp)
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                OutlinedTextField(
                    value = if (selectedGenres.isEmpty()) "Chưa chọn thể loại" else "${selectedGenres.size} thể loại đã chọn",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = "Thể loại",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { showGenreDialog = true },
                    shape = RoundedCornerShape(15.dp)
                )

                if (showGenreDialog) {
                    AlertDialog(
                        onDismissRequest = { showGenreDialog = false },
                        title = { Text(text = "Chọn thể loại") },
                        text = {
                            LazyColumn {
                                val genres = songState.genres ?: emptyList()
                                items(genres) { genre ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (selectedGenres.contains(genre.id)) {
                                                    selectedGenres.remove(genre.id)
                                                } else {
                                                    selectedGenres.add(genre.id)
                                                }
                                            }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedGenres.contains(genre.id),
                                            onCheckedChange = { isChecked ->
                                                if (isChecked) {
                                                    selectedGenres.add(genre.id)
                                                } else {
                                                    selectedGenres.remove(genre.id)
                                                }
                                            }
                                        )
                                        Text(text = genre.name, modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showGenreDialog = false }) {
                                Text("Xong")
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Button(
                    onClick = {
                        if(nameSong.isEmpty() || description.isEmpty() || duration.isEmpty() || releasedDate.isEmpty()) {
                            val errorMessage = context.getString(R.string.thong_bao_khong_de_trong)
                            Toast.makeText(
                                context,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            songViewModel.createSong(nameSong, description, duration.toInt(), releasedDate, selectedGenres.toList())
                            val successMessage = context.getString(R.string.tao_bai_hat_thanh_cong)
                            Toast.makeText(
                                context,
                                successMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            nameSong = ""
                            description = ""
                            duration = ""
                            releasedDate = ""
                            selectedGenres.clear()
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
    }
}