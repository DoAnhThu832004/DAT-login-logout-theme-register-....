package com.example.app.view.general

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R

@Composable
fun ThemePage(darkTheme: Boolean, onThemeUpdated: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DarkMode,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.padding(start = 16.dp))
        Text(
            text = stringResource(R.string.chu_de),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        ThemeScreen(
            darkTheme = darkTheme,
            onThemeUpdated = onThemeUpdated,
        )
    }
}
@Composable
fun ThemeSwitcher(
    darkTheme : Boolean = false,
    onClick: () -> Unit
) {
    val offset by animateDpAsState(
        targetValue = if(darkTheme) 0.dp else 30.dp,
        animationSpec = tween(durationMillis = 300)
    )
    Box(
        modifier = Modifier
            .width(60.dp)
            .height(30.dp)
            .clickable { onClick() }
            .clip(shape = CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(x = offset)
                .padding(5.dp)
                .clip(shape = CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) { }
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = CircleShape
                )
        ) {
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(10.dp),
                    imageVector = Icons.Default.Nightlight,
                    contentDescription = null,
                    tint = if(darkTheme) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(10.dp),
                    imageVector = Icons.Default.LightMode,
                    contentDescription = null,
                    tint = if(darkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}
@Composable
fun ThemeScreen(darkTheme: Boolean, onThemeUpdated: () -> Unit) {
    ThemeSwitcher(
        darkTheme = darkTheme,
        onClick = onThemeUpdated
    )
}