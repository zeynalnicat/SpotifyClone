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
            accessToken = "BQDp_GAi63RRa7w2Um28634fSu5_px_WxgC3ye3zhTApTeAVVC1a_mHPwxPXmkEmAQ8R36YoIQpy7n1Rk3dzdtkMbF0v8vi6FpRJ4D41WycO9m_Vvs1MFKT2Tpax2vZVgzvqMIrIHd6gaOF0yC4HQIvd-DBwcRQ1YzH3Z0SwIuX0IJo_1eL4wkQJHZlRmvJqcJLCPfuA9Dfhy8B2aRgnE4jo5_Wvv1G5iz6LQWy-ZYmTHFiA1hbYbvF-K1EeeG9xHR0sAOgEN8Kocg",
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
