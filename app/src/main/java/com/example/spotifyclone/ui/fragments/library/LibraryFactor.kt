package com.example.spotifyclone.ui.fragments.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LibraryFactor(private val roomDB: RoomDB, private val albumApi: AlbumApi, private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(roomDB, albumApi,firebaseAuth,firestore) as T
    }
}