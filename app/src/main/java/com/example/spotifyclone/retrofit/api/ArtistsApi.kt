package com.example.spotifyclone.retrofit.api

import com.example.spotifyclone.model.artist.ArtistsApiModel
import com.example.spotifyclone.model.artist.SearchedArtists
import retrofit2.Response
import retrofit2.http.GET


interface ArtistsApi {


    @GET("artists")
    suspend fun getArtists(@retrofit2.http.Query("ids") ids: String):Response<ArtistsApiModel>

    @GET("search?type=artist")
    suspend fun searchArtist(@retrofit2.http.Query("q") query: String) : Response<SearchedArtists>
}