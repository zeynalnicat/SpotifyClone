package com.example.spotifyclone.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.data.network.TokenRefresher
import com.example.spotifyclone.data.network.api.AlbumApi
import com.example.spotifyclone.data.network.api.ArtistsApi
import com.example.spotifyclone.data.network.api.deezer.TrackApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFactory(
    private val albumApi: AlbumApi,
    private val artistApi: ArtistsApi,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val tokenRefresher: TokenRefresher,
    private val trackApi: TrackApi,
    private val albumDeezerApi: com.example.spotifyclone.data.network.api.deezer.AlbumApi
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(albumApi, artistApi, firestore, firebaseAuth, tokenRefresher,trackApi,albumDeezerApi) as T
    }
}