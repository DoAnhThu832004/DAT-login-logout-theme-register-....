package com.example.app.view.general

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.example.app.R
import com.example.app.viewmodel.PlaylistViewModel
import com.example.app.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectReportBottomSheet(
    reportViewModel: ReportViewModel,
    albumId: String,
    title: String,
    check: Boolean
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var expanded1 by remember { mutableStateOf(false) }
    val options1 = listOf("SONG","ALBUM","PLAYLIST","ARTIST","SYSTEM","OTHER")
    var selectedOption by remember { mutableStateOf(options1[0]) }
    val options2 = listOf("MISSING_AUDIO","MISSING_IMAGE","WRONG_INFO","OTHER")
    var selectedOption1 by remember { mutableStateOf(options2[0]) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp) // Hoặc fillMaxHeight(0.7f)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
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
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(15.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options1.forEach { it ->
                    DropdownMenuItem(
                        text = { Text(text = it) },
                        onClick = {
                            selectedOption = it
                            expanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded1,
            onExpandedChange = { expanded1 = !expanded1 }
        ) {
            OutlinedTextField(
                value = selectedOption1,
                onValueChange = {},
                readOnly = true,
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
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(15.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded1,
                onDismissRequest = { expanded1 = false }
            ) {
                options2.forEach { it ->
                    DropdownMenuItem(
                        text = { Text(text = it) },
                        onClick = {
                            selectedOption1 = it
                            expanded1 = false
                        }
                    )
                }
            }
        }
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
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (description.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else {
                    reportViewModel.createReport(targetType = selectedOption, targetId = albumId, issueType = selectedOption1,description = description)
                    Toast.makeText(context, "Báo cáo thành công", Toast.LENGTH_SHORT).show()
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