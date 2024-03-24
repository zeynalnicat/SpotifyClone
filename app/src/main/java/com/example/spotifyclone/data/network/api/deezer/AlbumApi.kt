package com.example.spotifyclone.data.network.api.deezer

import com.example.spotifyclone.domain.model.deezer.album.AlbumDeezer
import com.example.spotifyclone.domain.model.dto.Album
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumApi {


    @GET("album/{id}")
    suspend fun getAlbum(@Path("id") id:Int):Response<com.example.spotifyclone.domain.model.deezer.album.AlbumDeezer>
}