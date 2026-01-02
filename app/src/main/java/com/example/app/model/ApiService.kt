package com.example.app.model

import android.content.Context
import com.example.app.model.request.AlbumCreationRequest
import com.example.app.model.request.AlbumUpdateRequest
import com.example.app.model.request.AuthenticationRequest
import com.example.app.model.request.LogoutRequest
import com.example.app.model.request.RefreshRequest
import com.example.app.model.request.SongCreationRequest
import com.example.app.model.request.SongUpdateRequest
import com.example.app.model.request.UserCreationRequest
import com.example.app.model.request.UserUpdateRequest
import com.example.app.model.response.Album
import com.example.app.model.response.ApiError
import com.example.app.model.response.ApiResponse
import com.example.app.model.response.Artist
import com.example.app.model.response.AuthenticationResponse
import com.example.app.model.response.Song
import com.example.app.model.response.UserResponse
import com.example.app.viewmodel.SessionManager
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/token")
    suspend fun authenticate(@Body request: AuthenticationRequest): Response<AuthenticationResponse>
    @POST("users")
    suspend fun createUser(@Body request: UserCreationRequest): Response<UserResponse>
    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<ApiError>
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<AuthenticationResponse>
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id : String, @Body request: UserUpdateRequest): Response<UserResponse>
    @GET("users/my-info")
    suspend fun getUserInfo(): Response<UserResponse>
    @GET("songs")
    suspend fun getSongs(): Response<ApiResponse<List<Song>>>
    @POST("songs")
    suspend fun createSong(@Body request: SongCreationRequest): Response<ApiResponse<Song>>
    @GET("songs/searchKey")
    suspend fun searchSongs(
        @Query("name") name: String
    ): Response<ApiResponse<List<Song>>>
    @DELETE("songs/{id}")
    suspend fun deleteSong(@Path("id") id: String): Response<ApiError>
    @Multipart
    @POST("songs/{id}/upload")
    suspend fun uploadSongFiles(
        @Path("id") id: String,
        @Part image: MultipartBody.Part,
        @Part audio: MultipartBody.Part
    ): Response<ApiResponse<Song>>
    @PUT("songs/{id}")
    suspend fun updateSong(
        @Path("id") id: String,
        @Body request: SongUpdateRequest
    ): Response<ApiResponse<Song>>
    @GET("albums")
    suspend fun getAlbums(): Response<ApiResponse<List<Album>>>
    @GET("albums/searchKey")
    suspend fun searchAlbums(
        @Query("name") name: String
    ): Response<ApiResponse<List<Album>>>
    @POST("albums")
    suspend fun createAlbum(@Body request: AlbumCreationRequest): Response<ApiResponse<Album>>
    @DELETE("albums/{id}")
    suspend fun deleteAlbum(@Path("id") id: String): Response<ApiError>
    @PUT("albums/{id}")
    suspend fun updateAlbum(
        @Path("id") id: String,
        @Body request: AlbumUpdateRequest
    ): Response<ApiResponse<Album>>
    @DELETE("albums/{albumId}/songs/{songId}")
    suspend fun deleteSongFromAlbum(
        @Path("albumId") albumId: String,
        @Path("songId") songId: String
    ) : Response<ApiError>
    @PUT("albums/{albumId}/songs/{songId}")
    suspend fun addSongToAlbum(
        @Path("albumId") albumId: String,
        @Path("songId") songId: String
    ): Response<ApiResponse<String>>
    @GET("artists")
    suspend fun getArtists(): Response<ApiResponse<List<Artist>>>
    @GET("artists/searchKey")
    suspend fun searchArtists(
        @Query("name") name: String
    ): Response<ApiResponse<List<Artist>>>
}
