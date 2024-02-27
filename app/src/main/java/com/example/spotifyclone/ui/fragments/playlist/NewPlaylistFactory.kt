package com.example.spotifyclone.ui.fragments.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class NewPlaylistFactory(private val firebaseAuth: FirebaseAuth,private val firebaseFirestore: FirebaseFirestore):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewPlaylistViewModel(firebaseAuth,firebaseFirestore) as T

    }
}