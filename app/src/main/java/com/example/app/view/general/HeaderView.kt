package com.example.app.view.general

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Artist
import com.example.app.viewmodel.ArtistViewModel

@Composable
fun HeaderView(
    modifier: Modifier = Modifier,
    name: String,
    image: String?,
    top : Int,
    check: Boolean,
    artistViewModel: ArtistViewModel,
    artist: Artist = Artist(
        id = "",
        name = "",
        imageUrlAr = "",
        description = "",
        songs = emptyList(),
        albums = emptyList(),
        totalFollowers = 0,
        followed = false
    )
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = top.dp, start = 32.dp, end = 24.dp)
    ) {
        if(image.isNullOrEmpty()) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(end = 16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(50.dp))
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(image),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(end = 16.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(50.dp))
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
                    text = "Xin chào",
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
                            artistViewModel.toggleFollow(artist)
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