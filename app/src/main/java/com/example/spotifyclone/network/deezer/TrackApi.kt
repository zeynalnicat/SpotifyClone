package com.example.spotifyclone.network.deezer

import com.example.spotifyclone.model.deezer.DeezerTrack
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackApi {

    @GET("track/{id}")
    suspend fun getTrack(@Path("id") id: Int): Response<DeezerTrack>

}