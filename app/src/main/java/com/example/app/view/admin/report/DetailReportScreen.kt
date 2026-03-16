package com.example.app.view.admin.report

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.example.app.model.response.Report
import com.example.app.viewmodel.SongViewModel

@Composable
fun DetailReportScreen(
    report: Report,
    songViewModel: SongViewModel
) {
    val songState by songViewModel.songState
    val songs = songState.songs ?: emptyList()
    val currentSong = remember(songs,report.targetId) {
        songs.find { it.id == report.targetId }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("Do anh thu")
        Text(text = "Report: ${report.description}")
        Image(
            painter = rememberAsyncImagePainter(currentSong?.imageUrl),
            contentDescription = null
        )
        Column(
        ) {
            currentSong?.let {
                Text(
                    text = it.name
                )
            }
            Text(
                text = report.issueType
            )
            Text(
                text = report.description
            )
        }
    }
}