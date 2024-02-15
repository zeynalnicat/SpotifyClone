package com.example.spotifyclone.di

import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.network.retrofit.api.ArtistsApi
import com.example.spotifyclone.network.retrofit.api.CategoriesApi
import com.example.spotifyclone.network.retrofit.consts.ConstValues
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(ConstValues.url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttp(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val authRequest = request.newBuilder().addHeader(
                "Authorization", "Bearer ${ConstValues.token}}"
            ).build()
            chain.proceed(authRequest)
        }

    }


    @Singleton
    @Provides
    fun provideAlbumApi(retrofit: Retrofit): AlbumApi = retrofit.create(AlbumApi::class.java)

    @Singleton
    @Provides
    fun provideArtistsApi(retrofit: Retrofit): ArtistsApi = retrofit.create(ArtistsApi::class.java)

    @Singleton
    @Provides
    fun provideCategoriesApi(retrofit: Retrofit): CategoriesApi =
        retrofit.create(CategoriesApi::class.java)


}