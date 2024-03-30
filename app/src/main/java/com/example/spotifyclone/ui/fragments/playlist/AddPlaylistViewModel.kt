package com.example.spotifyclone.ui.fragments.playlist

import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.domain.model.dto.MusicItem

import com.example.spotifyclone.domain.model.dto.PlaylistModel
import com.example.spotifyclone.domain.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale


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

        _playlists.postValue(Resource.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val playlistModels = playlistsModel()
                val playlistMap = playlistTrackCounts()

                playlistModels.forEach {
                    it.countTrack = playlistMap[it.id] ?: 0
                }

                _playlists.postValue(Resource.Success(playlistModels))
            } catch (e: Exception) {
                _playlists.postValue(Resource.Error(e))
            }


        }


    }

    suspend fun playlistsModel(): List<com.example.spotifyclone.domain.model.dto.PlaylistModel> {
        val playlistsRef = firestore.collection("playlists")
        val userId = firebaseAuth.currentUser?.uid
        val query = playlistsRef.whereEqualTo("userId", userId).get().await()
        val playlistList = mutableListOf<com.example.spotifyclone.domain.model.dto.PlaylistModel>()

        if (query != null && !query.isEmpty) {
            val documents = query.documents
            for (document in documents) {
                playlistList.add(
                    com.example.spotifyclone.domain.model.dto.PlaylistModel(
                        document["id"].toString(),
                        document["name"].toString(),
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


    fun addFirebase(playlist: List<com.example.spotifyclone.domain.model.dto.PlaylistModel>, track: com.example.spotifyclone.domain.model.dto.MusicItem) {
        val playlistsRef = firestore.collection("playlistTracks")
        val userId = firebaseAuth.currentUser?.uid
        val listStates = mutableListOf<Boolean>()
        try {
            viewModelScope.launch(Dispatchers.IO) {
                playlist.forEach {
                    val model = hashMapOf(
                        "artist" to track.artist,
                        "imgUri" to track.img,
                        "playlistId" to it.id,
                        "trackName" to track.name,
                        "trackUri" to track.trackUri,
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


    fun search(text: String) {
        val model = _playlists.value

        if (model is Resource.Success) {
            if (text.isEmpty()) {
                getPlaylists()
            } else {
                val searchResult = model.data.filter { it.name.contains(text, ignoreCase = true) }
                _playlists.postValue(Resource.Success(searchResult))
            }
        }


    }


}