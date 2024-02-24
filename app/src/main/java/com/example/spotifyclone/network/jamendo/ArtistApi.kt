package com.example.spotifyclone.network.jamendo

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtistApi {


  @GET("artists")
  suspend fun getArtists(@Query("client_id") clientId :String , @Query("format") format:String = "jsonpretty")
}