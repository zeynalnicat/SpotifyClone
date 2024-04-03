package com.example.spotifyclone.ui.fragments.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.data.network.api.AlbumApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LibraryFactor(
    private val albumApi: AlbumApi,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val deezerAlbumApi: com.example.spotifyclone.data.network.api.deezer.AlbumApi
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(albumApi, firebaseAuth, firestore,deezerAlbumApi) as T
    }
}