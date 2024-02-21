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

    private val _state = MutableLiveData<Resource<List<Boolean>>>()

    val playlists: LiveData<Resource<List<PlaylistModel>>>
        get() = _playlists

    val state: LiveData<Resource<List<Boolean>>> get() = _state


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
                    playlistList.add(
                        PlaylistModel(
                            document["id"].toString(),
                            document["name"].toString()
                        )
                    )
                }
                _playlists.postValue(Resource.Success(playlistList))
            } else {
                _playlists.postValue(Resource.Error(Exception("There is no data from playlist")))
            }
        }

    }


    fun addFirebase(playlist: List<PlaylistModel>, trackId: String) {
        val playlistsRef = firestore.collection("playlistTracks")
        val userId = firebaseAuth.currentUser?.uid
        val listStates = mutableListOf<Boolean>()
        try {
            viewModelScope.launch(Dispatchers.IO) {
                playlist.forEach {
                    val model = hashMapOf(
                        "playlistId" to it.id,
                        "trackId" to trackId,
                        "userId" to userId
                    )
                    playlistsRef.add(model)
                        .addOnSuccessListener {
                            listStates.add(true)
                        }
                        .addOnFailureListener {
                            listStates.add(false)
                        }
                }

                _state.postValue(Resource.Success(listStates))
            }

        } catch (e: Exception) {
            _state.postValue(Resource.Error(e))
        }


    }


}