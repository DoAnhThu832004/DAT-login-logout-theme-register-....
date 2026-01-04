package com.example.app.view.general

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.app.model.response.Artist
import com.example.app.model.response.Song
import com.example.app.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel,
    onArtistClick: (Artist) -> Unit = {},
    onSongClick: (Song) -> Unit = {}
) {
    //val suggestion by searchViewModel.suggestions.collectAsState()
    val sSong by searchViewModel.sSong.collectAsState()
    val sArtist by searchViewModel.sArtist.collectAsState()
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(sSong,sArtist) {
        if(sSong.isNotEmpty() || sArtist.isNotEmpty()) {
            expanded = true
        }
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {expanded =! expanded},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {new ->
                query = new
                searchViewModel.onQueryChanged(new)
                if(new.isEmpty()) expanded = false
            },
            placeholder = { Text("Tìm nghệ sĩ, bài hát...") },
            leadingIcon = {
                Icon(Icons.Default.Search,contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
        )
        if (sSong.isNotEmpty() || sArtist.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                sArtist.forEach { artist ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = artist.imageUrlAr,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(shape = RoundedCornerShape(50.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = artist.name
                                )
                            }
                        },
                        onClick = {
                            query = artist.name
                            expanded = false
                            searchViewModel.clearSuggestions()
                            onArtistClick(artist)
                        },
                        contentPadding = PaddingValues(8.dp)
                    )
                }
                sSong.forEach { songs ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = songs.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(shape = RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = songs.name
                                )
                            }
                        },
                        onClick = {
                            query = songs.name
                            expanded = false
                            searchViewModel.clearSuggestions()
                            onSongClick(songs)
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}