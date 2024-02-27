package com.example.spotifyclone.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.retrofit.TokenRefresher

import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.network.retrofit.api.ArtistsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFactory(
    private val albumApi: AlbumApi,
    private val artistApi: ArtistsApi,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val tokenRefresher: TokenRefresher
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(albumApi, artistApi, firestore, firebaseAuth, tokenRefresher) as T
    }
}