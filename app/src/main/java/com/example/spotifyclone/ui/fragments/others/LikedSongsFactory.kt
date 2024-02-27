package com.example.spotifyclone.ui.fragments.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LikedSongsFactory(private val firestore: FirebaseFirestore,private val firebaseAuth: FirebaseAuth) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LikedSongsViewModel(firestore,firebaseAuth) as T
    }
}