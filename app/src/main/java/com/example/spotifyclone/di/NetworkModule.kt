package com.example.spotifyclone.di

import com.example.spotifyclone.data.network.TokenRefresher
import com.example.spotifyclone.data.network.api.AlbumApi
import com.example.spotifyclone.data.network.api.ArtistsApi
import com.example.spotifyclone.data.network.api.deezer.SearchApi
import com.example.spotifyclone.data.network.api.deezer.TrackApi
import com.example.spotifyclone.data.network.consts.ConstValues
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

const val url = "https://api.spotify.com/v1/"
const val urlDeezer = "https://api.deezer.com/"
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
    fun provideConstValues(): ConstValues {
        return ConstValues(
            clientId = "3927891f65e6435586508e1bc22ce82a",
            clientSecret = "8aac71670abc416e99adf6012dd96ad8",
            redirectUri = "https://oauth.pstmn.io/v1/browser-callback",
            accessToken = "BQDE7e8O1faQdBo6cjn7N0JktoF7QF3dMXI6W2VINIo3W298OVSLIYyjmTv5PfZP4eaxmCaBs25JQCSUu_rTsb9MHsRO4rJnoPfnZec3KZuPFh9AZ8OgJ4x3PH3_I5NWpaQuYEr7JShadWfNiy4CL4DfPSQg8df62Oq4MFXs1lWNShqiVmpghmNLNbyNbSJGm9rwsAdOBPJ-h_gQqu_KS9dmfQgyUM05ObUSYEsvwKmWbTZPNiVoHMUvpXx8h32ezqUoXqSTqrIWSw",
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


    @Named("DeezerApi")
    @Singleton
    @Provides
    fun provideDeezerRetrofit():Retrofit{
        return Retrofit.Builder()
            .baseUrl(urlDeezer)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideSearchApi(@Named("DeezerApi") retrofit: Retrofit): SearchApi = retrofit.create(
        SearchApi::class.java)

    @Singleton
    @Provides
    fun provideTrackApi(@Named("DeezerApi") retrofit: Retrofit): TrackApi = retrofit.create(TrackApi::class.java)

    @Singleton
    @Provides
    fun provideDeezerAlbumApi(@Named("DeezerApi") retrofit: Retrofit): com.example.spotifyclone.data.network.api.deezer.AlbumApi = retrofit.create(
        com.example.spotifyclone.data.network.api.deezer.AlbumApi::class.java)


}
