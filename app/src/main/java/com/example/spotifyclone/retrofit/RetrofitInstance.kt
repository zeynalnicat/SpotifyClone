package com.example.spotifyclone.retrofit

import com.example.spotifyclone.retrofit.api.AlbumApi
import com.example.spotifyclone.retrofit.api.ArtistsApi
import com.example.spotifyclone.retrofit.api.CategoriesApi
import com.example.spotifyclone.retrofit.consts.ConstValues
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val url = "https://api.spotify.com/v1/"

object RetrofitInstance {
    private var token = ConstValues.token
    private var retrofitInstance: Retrofit? = null

    fun getInstance(): Retrofit? {
        if (retrofitInstance == null) {
            val okHttpClient =
                OkHttpClient().newBuilder().addInterceptor(TokenInterceptor(token)).build()
            retrofitInstance = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitInstance
    }

}

class TokenInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authRequest = request.newBuilder().addHeader(
            "Authorization", "Bearer $token"
        ).build()
        return chain.proceed(authRequest)
    }
}