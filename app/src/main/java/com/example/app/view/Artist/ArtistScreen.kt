package com.example.app.view.Artist

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.model.response.Song
import com.example.app.view.Album.AlbumItem
import com.example.app.view.general.HeaderView
import com.example.app.viewmodel.AlbumViewModel
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.EditProfileViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ArtistScreen(
    artist: Artist,
    onSongClick: (Song) -> Unit,
    onBack: () -> Unit,
    albumViewModel: AlbumViewModel,
    artistViewModel: ArtistViewModel,
    editProfileViewModel: EditProfileViewModel,
    onAlbumClick: (Album) -> Unit,
) {
    val context = LocalContext.current
    val albumState by albumViewModel.albumState.collectAsState()
    val currentArtistState by artistViewModel.currentArtist.collectAsState()

    // 2. Tạo displayArtist ưu tiên lấy từ ViewModel
    val displayArtist = currentArtistState ?: artist
    LaunchedEffect(artist.id) {
        artistViewModel.initCurrentArtist(artist)
        //albumViewModel.getAlbumByArtist(artist.id)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            ConstraintLayout {
                val (topImg, proFile,backBtn) = createRefs()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .constrainAs(topImg) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ), shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                )
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp)
                        .clickable { onBack() } // Gọi callback khi nhấn
                        .constrainAs(backBtn) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HeaderView(
                    name = displayArtist.name,
                    image = displayArtist.imageUrlAr,
                    top = 48,
                    check = false,
                    artist = displayArtist,
                    onImageSelected = {
                        editProfileViewModel.uploadImage(displayArtist.id,it,context)
                    },
                    onToggleFollowClick = {
                        artistViewModel.toggleFollow(displayArtist)
                    }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, start = 24.dp, end = 24.dp)
                        .shadow(3.dp, shape = RoundedCornerShape(20.dp))
                        .constrainAs(proFile) {
                            top.linkTo(topImg.bottom)
                            bottom.linkTo(topImg.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                }
            }
        }
        item{
            val previewSongs = displayArtist.songs.take(6)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "Gợi ý bài hát",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
//                Text(
//                    text = "Tất cả >",
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .clickable {
//
//                        }
//                        .padding(horizontal = 8.dp, vertical = 4.dp)
//                )

            }
            SongsGrid(
                songs = previewSongs,
                height = when (previewSongs.size) {
                    in 1..2 -> 80.dp
                    in 3..4 -> 160.dp
                    else -> 240.dp
                },
                onSongClick = {
                    onSongClick(it)
                }
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = "Album",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayArtist.albums) { album ->
                        Box(modifier = Modifier.width(140.dp)) {
                            AlbumItem(
                                album = album,
                                onClick = {
                                    onAlbumClick(album)
                                }
                            )
                        }
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "Về ${displayArtist.name}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                AsyncImage(
                    model = displayArtist.imageUrlAr,
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = displayArtist.name
                )
            }
        }
    }
}