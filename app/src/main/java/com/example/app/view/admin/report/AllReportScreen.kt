package com.example.app.view.admin.report

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.app.model.response.Report
import com.example.app.viewmodel.ReportViewModel
import com.example.app.viewmodel.SongViewModel

@Composable
fun AllReportScreen(
    reports: List<Report>,
    reportViewModel: ReportViewModel,
    songViewModel: SongViewModel,
    onBack: () -> Unit
) {

    LaunchedEffect(key1 = Unit) {
        reportViewModel.getReport()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {onBack()}
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Phản hồi của người dùng",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
        items(reports) {
            DetailReportScreen(it,songViewModel)
        }
    }
}