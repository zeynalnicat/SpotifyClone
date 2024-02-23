package com.example.spotifyclone.ui.fragments.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import com.example.spotifyclone.model.dto.PlaylistModel
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserLibraryViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _playlists = MutableLiveData<Resource<List<PlaylistModel>>>()

    val playlists: LiveData<Resource<List<PlaylistModel>>>
        get() = _playlists

    fun getPlaylists() {

         _playlists.postValue(Resource.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val playlistModels = playlistsModel()
                val playlistMap = playlistTrackCounts()

                playlistModels.forEach {
                    it.countTrack = playlistMap[it.id] ?: 0
                }

                _playlists.postValue(Resource.Success(playlistModels))
            }catch (e:Exception){
                _playlists.postValue(Resource.Error(e))
            }


        }


    }

    suspend fun playlistsModel(): List<PlaylistModel> {
        val playlistsRef = firestore.collection("playlists")
        val userId = firebaseAuth.currentUser?.uid
        val query = playlistsRef.whereEqualTo("userId", userId).get().await()
        val playlistList = mutableListOf<PlaylistModel>()

        if (query != null && !query.isEmpty) {
            val documents = query.documents
            for (document in documents) {
                playlistList.add(
                    PlaylistModel(
                        document["id"].toString(),
                        document["name"].toString(),
                        isLibrary = true
                    )
                )
            }


        }
        return playlistList
    }

    suspend fun playlistTrackCounts(): Map<String, Int> {
        val userId = firebaseAuth.currentUser?.uid
        val playlistRef = firestore.collection("playlistTracks")
        val query = playlistRef.whereEqualTo("userId", userId).get().await()
        val playlistMap = mutableMapOf<String, Int>()

        if (query != null && !query.isEmpty) {
            val documents = query.documents
            for (document in documents) {
                val subQuery = playlistRef.whereEqualTo("userId", userId)
                    .whereEqualTo("playlistId", document["playlistId"].toString()).get().await()

                playlistMap[document["playlistId"].toString()] = subQuery.size()
            }
        }
        return playlistMap
    }

}