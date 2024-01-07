package com.example.spotifyclone.retrofit.api

import com.example.spotifyclone.model.album.newrelease.Albums
import com.example.spotifyclone.model.album.singlealbum.Album
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumApi {
    @GET("browse/new-releases")
    suspend fun getNewReleases(): Response<Albums>

    @GET("albums")
    suspend fun getSomeAlbums(@retrofit2.http.Query("ids") query: String): Response<com.example.spotifyclone.model.album.popularalbums.Albums>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id:String):Response<Album>
}