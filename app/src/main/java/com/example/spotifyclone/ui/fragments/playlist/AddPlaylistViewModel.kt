package com.example.spotifyclone.ui.fragments.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.dto.MusicItem

import com.example.spotifyclone.model.dto.PlaylistModel
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPlaylistViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _playlists = MutableLiveData<Resource<List<PlaylistModel>>>()

    val playlists: LiveData<Resource<List<PlaylistModel>>>
        get() = _playlists


    fun getPlaylists() {
        val playlistsRef = firestore.collection("playlists")
        val userId = firebaseAuth.currentUser?.uid
        val query = playlistsRef.whereEqualTo("userId", userId)

        query.get().addOnCompleteListener { task ->
            val playlistList = mutableListOf<PlaylistModel>()
            val result = task.result
            if (result != null && !result.isEmpty) {
                val documents = result.documents
                for (document in documents) {
                    playlistList.add(PlaylistModel(0, document["name"].toString()))
                }
                _playlists.postValue(Resource.Success(playlistList))
            } else {
                _playlists.postValue(Resource.Error(Exception("There is no data from playlist")))
            }
        }

    }


    fun addFirebase(tracks:List<MusicItem>){
        val playlistsRef = firestore.collection("playlistTracks")
        val userId = firebaseAuth.currentUser?.uid


    }
}