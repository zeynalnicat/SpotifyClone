package com.example.spotifyclone.data.network.api.deezer


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {


    @GET("search")
    suspend fun search(@Query("q") query:String):Response<com.example.spotifyclone.domain.model.deezer.search.Search>
}