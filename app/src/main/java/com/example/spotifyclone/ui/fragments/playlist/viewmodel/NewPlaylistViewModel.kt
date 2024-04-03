package com.example.spotifyclone.ui.fragments.playlist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyclone.domain.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class NewPlaylistViewModel(private val firebaseAuth: FirebaseAuth, private val firebaseFirestore: FirebaseFirestore) : ViewModel() {

    val isSuccessful = MutableLiveData<Resource<Boolean>>()

    fun insert(name: String) {
        isSuccessful.postValue(Resource.Loading)
        val playlistRef = firebaseFirestore.collection("playlists")
        val uuid = UUID.randomUUID().toString()
        val userId = firebaseAuth.currentUser?.uid
        val playlist = hashMapOf(
            "id" to uuid,
            "name" to name,
            "userId" to userId
        )
        playlistRef.add(playlist)
            .addOnSuccessListener {
                isSuccessful.postValue(Resource.Success(true))
            }
            .addOnFailureListener {
                isSuccessful.postValue(Resource.Error(it))
            }



    }
}