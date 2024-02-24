package com.example.spotifyclone.di

import com.example.spotifyclone.network.retrofit.TokenRefresher
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

const val url = "https://api.spotify.com/v1/"
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
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
    fun provideInterceptor(constValues: ConstValues): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val authRequest = request.newBuilder().addHeader(
                "Authorization", "Bearer ${constValues.accessToken}"
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

    @Singleton
    @Provides
    fun provideConstValues(): ConstValues {
        return ConstValues(
            clientId = "3927891f65e6435586508e1bc22ce82a",
            clientSecret = "8aac71670abc416e99adf6012dd96ad8",
            redirectUri = "https://oauth.pstmn.io/v1/browser-callback",
            accessToken = "BQAg7MFjhKhHZqpFEr4Vx3kWZlEs8UOPK4HLRly4dgEDBpfybrwhKbyfa04qQJRPr_Ykbe20NBmh_Ei_PyVSo7r-1jMViNhShLY47ZXyWcCSZS65p_nqS8E7SI3kxMs1oVm21Q_vrSi6ht_tsFuPfhlPgCQqutZrjO8hoSkEhAQtSj_bf-stIreJI9ikGVwdMSgcqGWn0D97zQJxaJWtE5xn2QeYkiXNsV29BXGwbdgOZsNpAICyJ6cE8NACKS2o0ydN6PoN2L6fCQ",
            tokenType = "Bearer",
            accessTokenUrl = "https://accounts.spotify.com/api/token",
            expiresIn = 3600,
            scope = "playlist-read-private playlist-modify-private playlist-modify-public",
            refreshToken = "AQCcpQBl8bBs7hEivJX3p_HHHXDnpRUFBvH2VCGgdGN8I-c9hCpXkDw6osUznNYz_zoazhpaTdPPZ9KAOyhThsteM3LmBWyI5DlruJVEHNp-8VDqge5yhPG2aa2uCZub__s",
            timestamp = 1708775443372
        )
    }
    @Singleton
    @Provides
    fun provideTokenRefresher(constValues: ConstValues, okHttpClient: OkHttpClient): TokenRefresher {
        return TokenRefresher(constValues, okHttpClient)
    }


}
