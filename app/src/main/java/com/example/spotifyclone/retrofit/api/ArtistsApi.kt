package com.example.spotifyclone.retrofit.api

import retrofit2.http.GET

interface ArtistsApi {


    @GET("artists")
    suspend fun getArtists()
}