package com.example.spotifyclone.ui.single_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SinglePlaylistViewModel(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _playlistName = MutableLiveData<Resource<String>>()

    val playlistName: LiveData<Resource<String>> get() = _playlistName


    fun getPlaylistName(id: String) {
        val userId = firebaseAuth.currentUser?.uid
        val playlistRef = firestore.collection("playlists")
        val query = playlistRef.whereEqualTo("userId", userId).whereEqualTo("id", id)

        query.get().addOnSuccessListener {
            if (!it.isEmpty && it != null) {
                val result = it.documents[0]
                _playlistName.postValue(Resource.Success(result["name"].toString()))
            }else{
                _playlistName.postValue(Resource.Error(Exception("Error")))
            }
        }.addOnFailureListener { e ->
            _playlistName.postValue(Resource.Error(e))
        }

    }
}