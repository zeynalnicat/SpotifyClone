package com.example.spotifyclone.ui.fragments.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class NewPlaylistViewModel(private val firebaseAuth: FirebaseAuth, private val firebaseFirestore: FirebaseFirestore) : ViewModel() {

    val isSuccessful = MutableLiveData<Resource<Boolean>>()

    fun insert(name: String) {
        isSuccessful.postValue(Resource.Loading)
        val playlistRef = firebaseFirestore.collection("playlists")
        val userId = firebaseAuth.currentUser?.uid
        val playlist = hashMapOf(
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