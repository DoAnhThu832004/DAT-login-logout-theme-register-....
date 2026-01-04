package com.example.app.view.admin.artist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.app.R
import com.example.app.model.response.Artist
import com.example.app.view.general.ConfirmDialog
import com.example.app.view.general.SearchBar
import com.example.app.viewmodel.ArtistViewModel
import com.example.app.viewmodel.SearchViewModel

@Composable
fun ListArtistScreen(
    modifier: Modifier = Modifier,
    artists : List<Artist>,
    searchViewModel: SearchViewModel,
    artistViewModel: ArtistViewModel,
    onArtistClick: (Artist) -> Unit,
    onUpdateClick: (Artist) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SearchBar(searchViewModel = searchViewModel)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(artists) { artist ->
                ListArtist(
                    artist = artist,
                    onArtistClick = { onArtistClick(artist) },
                    onDeleteClick = { artistId ->
                        artistViewModel.deleteArtist(artistId)
                    },
                    onUpdateClick = {
                        onUpdateClick(artist)
                    }
                )
            }
        }
    }
}
@Composable
fun ListArtist(
    artist: Artist,
    onArtistClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    onUpdateClick: () -> Unit
) {
    var show by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { onArtistClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(artist.imageUrlAr),
                contentDescription = null,
                modifier = Modifier
                    .width(70.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(50.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = artist.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {onUpdateClick()}
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {
                    show = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    }
    ConfirmDialog(
        showDialog = show,
        icon = Icons.Default.Notifications,
        iconColor = Color.Yellow,
        title = stringResource(R.string.xac_nhan),
        message = stringResource(R.string.tieu_de_xoa_bai_hat),
        confirmText = stringResource(R.string.xac_nhan),
        dismissText = stringResource(R.string.quay_lai),
        onConfirm = {
            show = false
            onDeleteClick(artist.id)
        },
        onDismiss = {
            show = false
        }
    )
}