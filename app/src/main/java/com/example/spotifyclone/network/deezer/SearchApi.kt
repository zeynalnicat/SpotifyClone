package com.example.spotifyclone.network.deezer

import com.example.spotifyclone.model.deezer.search.Search
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {


    @GET("search")
    suspend fun search(@Query("q") query:String):Response<Search>
}