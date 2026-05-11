package com.example.app.model.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app.model.ApiService
import com.example.app.model.response.Album
import com.example.app.model.response.Song

class AlbumPagingSource (
    private val apiService: ApiService
) : PagingSource<Int, Album>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getAlbums(page, params.loadSize)
            val albums = response.body()?.result?.result ?: emptyList()
            LoadResult.Page(
                data = albums,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (albums.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? = null
}