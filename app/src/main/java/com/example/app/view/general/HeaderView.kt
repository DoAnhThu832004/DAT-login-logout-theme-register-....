package com.example.app.view.general

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.PersonRemoveAlt1
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Artist
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.EditProfileViewModel

@Composable
fun HeaderView(
    modifier: Modifier = Modifier,
    name: String,
    image: String?,
    top : Int,
    check: Boolean,
    artist: Artist = Artist(
        id = "",
        name = "",
        imageUrlAr = "",
        description = "",
        songs = emptyList(),
        albums = emptyList(),
        totalFollowers = 0,
        followed = false
    ),
    onImageSelected: (Uri) -> Unit = {},
    onToggleFollowClick: (Artist) -> Unit = {}
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {onImageSelected(it)}
    }

    val painter = when {
        imageUri != null -> rememberAsyncImagePainter(imageUri)
        !image.isNullOrEmpty() -> rememberAsyncImagePainter(image)
        else -> null
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = top.dp, start = 32.dp, end = 24.dp)
    ) {
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(end = 16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    }
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(end = 16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    }
            )
        }
        Column(
            modifier = Modifier
                .height(100.dp)
                .padding(start = 14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            if(check) {
                Text(
                    text = stringResource(R.string.xin_chao),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Text(
                text = name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            if(!check) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${artist.totalFollowers} người quan tâm",
                        color = Color.White,
                    )
                    IconButton(
                        onClick = {
                            onToggleFollowClick(artist)
                        }
                    ) {
                        Icon(
                            imageVector = if(!artist.followed) Icons.Default.PersonAddAlt1 else Icons.Default.PersonRemoveAlt1,
                            contentDescription = null,
                            //tint = if (.favorite) Color.Red else Color.Gray
                        )
                    }
                }
            }
        }
    }
}