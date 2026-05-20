package com.example.app.model.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app.model.ApiService
import com.example.app.model.response.Song

class SongPagingSource(
    private val apiService: ApiService,
    private val query: String? = null,
    private val genreId: String? = null
) : PagingSource<Int, Song>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        return try {
            val page = params.key ?: 1

            val response = when {
                !query.isNullOrEmpty() -> apiService.searchSongsForAdmin(query, page, params.loadSize)
                !genreId.isNullOrEmpty() -> apiService.getSongs(page, params.loadSize, genreId)
                else -> apiService.getSongs(page, params.loadSize)
            }
            val songs = response.body()?.result?.result ?: emptyList()

            LoadResult.Page(
                data = songs,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (songs.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? = null
}