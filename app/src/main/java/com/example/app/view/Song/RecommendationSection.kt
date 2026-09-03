package com.example.app.view.Song

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.model.response.HomeRecommendationResponse
import com.example.app.model.response.Playlist
import com.example.app.model.response.Song
import com.example.app.viewmodel.RecommendationViewModel
import com.example.test_ms.view.SongItem

// ──────────────────────────────────────────────────────────
//  Tiêu đề động dựa theo source
// ──────────────────────────────────────────────────────────
private data class SectionTitles(
    val songs: String,
    val artists: String,
    val albums: String,
    val playlists: String
)

private fun titlesForSource(source: String): SectionTitles = when (source) {
    "PERSONALIZED" -> SectionTitles(
        songs     = "✨ Dành riêng cho bạn",
        artists   = "🎤 Nghệ sĩ bạn có thể thích",
        albums    = "💿 Album hợp gu bạn",
        playlists = "📋 Playlist tuyển chọn cho bạn"
    )
    "COLD_START_GENRE" -> SectionTitles(
        songs     = "🔥 Thịnh hành theo sở thích của bạn",
        artists   = "⭐ Nghệ sĩ nổi bật",
        albums    = "🎵 Album hot",
        playlists = "📻 Playlist hệ thống"
    )
    else -> SectionTitles(  // COLD_START_GLOBAL
        songs     = "🌏 Top bài hát thịnh hành",
        artists   = "⭐ Nghệ sĩ nổi bật",
        albums    = "💿 Album hot",
        playlists = "🔥 Playlist nổi bật"
    )
}

// ──────────────────────────────────────────────────────────
//  Main composable
// ──────────────────────────────────────────────────────────
@Composable
fun RecommendationSection(
    recommendationViewModel: RecommendationViewModel,
    onSongClick: (Song) -> Unit,
    onArtistClick: ((Artist) -> Unit)? = null,
    onAlbumClick: ((Album) -> Unit)? = null,
    onPlaylistClick: ((Playlist) -> Unit)? = null
) {
    val state by recommendationViewModel.state.collectAsState()

    // ── Case 1: Home recommendation (API mới) ──
    val home = state.homeRecommendation
    if (state.isLoadingHome) {
        HomeSkeletonSection()
        return
    }
    if (home != null) {
        HomeRecommendationContent(
            home = home,
            onSongClick = onSongClick,
            onArtistClick = onArtistClick,
            onAlbumClick = onAlbumClick,
            onPlaylistClick = onPlaylistClick
        )
        return
    }

    // ── Case 2: Fallback — song-only recommendation (API cũ) ──
    if (state.error != null && state.recommendations == null) return

    val oldRec = state.recommendations ?: return
    if (oldRec.songs.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = when (oldRec.source) {
                "PERSONALIZED"     -> "✨ Gợi ý cho bạn"
                "COLD_START_GENRE" -> "🎵 Thịnh hành theo sở thích"
                else               -> "🔥 Top bài hát thịnh hành"
            },
            badge = null
        )
        if (state.isLoading) {
            ShimmerRow()
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(oldRec.songs, key = { it.id }) { song ->
                    Box(modifier = Modifier.width(140.dp)) {
                        SongItem(song = song, onClick = { onSongClick(song) })
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
//  Home recommendation — 4 carousel sections
// ──────────────────────────────────────────────────────────
@Composable
private fun HomeRecommendationContent(
    home: HomeRecommendationResponse,
    onSongClick: (Song) -> Unit,
    onArtistClick: ((Artist) -> Unit)?,
    onAlbumClick: ((Album) -> Unit)?,
    onPlaylistClick: ((Playlist) -> Unit)?
) {
    val titles = titlesForSource(home.source)

    // ── Songs section ──
    if (home.recommendedSongs.isNotEmpty()) {
        SectionHeader(title = titles.songs, badge = home.source)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(home.recommendedSongs, key = { it.id }) { song ->
                Box(modifier = Modifier.width(140.dp)) {
                    SongItem(song = song, onClick = {
                        com.example.app.analytics.AnalyticsHelper.logHomeSectionClick("recommended_songs", song.id.toString())
                        onSongClick(song)
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    // ── Artists section (ẩn nếu rỗng) ──
    if (home.recommendedArtists.isNotEmpty()) {
        SectionHeader(title = titles.artists, badge = null)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(home.recommendedArtists, key = { it.id }) { artist ->
                ArtistRecommendItem(
                    artist = artist,
                    onClick = {
                        com.example.app.analytics.AnalyticsHelper.logHomeSectionClick("recommended_artists", artist.id)
                        onArtistClick?.invoke(artist)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    // ── Albums section (ẩn nếu rỗng) ──
    if (home.recommendedAlbums.isNotEmpty()) {
        SectionHeader(title = titles.albums, badge = null)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(home.recommendedAlbums, key = { it.id }) { album ->
                AlbumRecommendItem(
                    album = album,
                    onClick = {
                        com.example.app.analytics.AnalyticsHelper.logHomeSectionClick("recommended_albums", album.id)
                        onAlbumClick?.invoke(album)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

    // ── Playlists section (ẩn nếu rỗng) ──
    if (home.recommendedPlaylists.isNotEmpty()) {
        SectionHeader(title = titles.playlists, badge = null)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(home.recommendedPlaylists, key = { it.id }) { playlist ->
                PlaylistRecommendItem(
                    playlist = playlist,
                    onClick = {
                        com.example.app.analytics.AnalyticsHelper.logHomeSectionClick("recommended_playlists", playlist.id)
                        onPlaylistClick?.invoke(playlist)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ──────────────────────────────────────────────────────────
//  Section header với badge source
// ──────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, badge: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            if (badge != null) {
                val (badgeText, badgeColor) = when (badge) {
                    "PERSONALIZED"     -> "🎯 Cá nhân hóa" to Color(0xFF6C63FF)
                    "COLD_START_GENRE" -> "🎵 Theo sở thích" to Color(0xFF3EC6E0)
                    else               -> "🌏 Toàn cầu" to Color(0xFFFF6B6B)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────
//  Item cards
// ──────────────────────────────────────────────────────────
@Composable
private fun ArtistRecommendItem(artist: Artist, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, Color(0xFF6C63FF).copy(alpha = 0.4f), CircleShape)
        ) {
            AsyncImage(
                model = artist.imageUrlAr,
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = artist.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${artist.totalFollowers} người theo dõi",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlbumRecommendItem(album: Album, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = album.imageUrlA,
                contentDescription = album.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = album.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PlaylistRecommendItem(playlist: Playlist, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF6C63FF).copy(alpha = 0.7f), Color(0xFF3EC6E0).copy(alpha = 0.7f))
                    )
                )
        ) {
            AsyncImage(
                model = playlist.imageUrlP,
                contentDescription = playlist.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = playlist.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (!playlist.description.isNullOrBlank()) {
            Text(
                text = playlist.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ──────────────────────────────────────────────────────────
//  Skeleton loading
// ──────────────────────────────────────────────────────────
@Composable
private fun HomeSkeletonSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            )
            ShimmerRow()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ShimmerRow() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(4) { SongItemSkeleton() }
    }
}

@Composable
private fun SongItemSkeleton() {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Column(modifier = Modifier.width(140.dp)) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(11.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}
