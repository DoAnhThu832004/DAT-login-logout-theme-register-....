package com.example.app.model.response

data class HomeRecommendationResponse(
    val source: String,                          // "PERSONALIZED" | "COLD_START_GENRE" | "COLD_START_GLOBAL"
    val recommendedSongs: List<Song> = emptyList(),
    val recommendedArtists: List<Artist> = emptyList(),
    val recommendedAlbums: List<Album> = emptyList(),
    val recommendedPlaylists: List<Playlist> = emptyList()
)
