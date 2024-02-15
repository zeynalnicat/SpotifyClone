package com.example.spotifyclone.network.retrofit.api

import com.example.spotifyclone.model.categories.Categories
import retrofit2.Response
import retrofit2.http.GET

interface CategoriesApi {

    @GET("browse/categories")
    suspend fun getCategories():Response<Categories>
}