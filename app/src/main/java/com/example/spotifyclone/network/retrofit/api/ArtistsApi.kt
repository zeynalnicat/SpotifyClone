package com.example.spotifyclone.network.retrofit.api

import com.example.spotifyclone.model.album.newrelease.AlbumsX
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.model.artist.ArtistsApiModel
import com.example.spotifyclone.model.artist.SearchedArtists
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface ArtistsApi {


    @GET("artists")
    suspend fun getArtists(@retrofit2.http.Query("ids") ids: String):Response<ArtistsApiModel>

//    @GET("artists")
//    suspend fun getSingleArtist(@retrofit2.http.Query("ids") ids: String):Response<Artist>

    @GET("search?type=artist")
    suspend fun searchArtist(@retrofit2.http.Query("q") query: String) : Response<SearchedArtists>

    @GET("artists/{id}/albums")
    suspend fun getArtistAlbums(@Path("id") id:String):Response<AlbumsX>
}