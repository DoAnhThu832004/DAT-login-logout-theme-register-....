package com.example.app.view.general

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R

@Composable
fun HeaderView(
    modifier: Modifier = Modifier,
    name: String,
    top : Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = top.dp, start = 24.dp, end = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.7f)
                .height(100.dp)
                .padding(start = 14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.chao_mung),
                color = Color.White,
                fontSize = 18.sp
            )
            Text(
                text = name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .width(75.dp)
                .size(75.dp)
                .clickable {  }
                .padding(top = 16.dp)
        )
    }
}