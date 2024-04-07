package com.example.spotifyclone.ui.fragments.library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import com.example.spotifyclone.domain.model.dto.PlaylistModel
import com.example.spotifyclone.domain.resource.Resource
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
                val listPlaylist = mutableListOf<PlaylistModel>()

                playlistModels.forEach {
                    it.countTrack = playlistMap[it.id] ?: 0
                    listPlaylist.add(it)
                    _playlists.postValue(Resource.Success(listPlaylist))
                }
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
        }else{
            _playlists.postValue(Resource.Error(Exception("There was an error")))
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

    fun removeFromPlaylist(id:String){
        val userId = firebaseAuth.currentUser?.uid
        val playlistRef = firestore.collection("playlists")
        val query = playlistRef.whereEqualTo("userId",userId).whereEqualTo("id",id)
        viewModelScope.launch(Dispatchers.IO) {
            removeRelatedTracks(id)
            query.get().addOnSuccessListener {querySnapshot->
                if(!querySnapshot.isEmpty && querySnapshot!=null){
                    playlistRef.document(querySnapshot.documents[0].id).delete()
                    getPlaylists()
                }
            }

        }

    }

    suspend fun removeRelatedTracks(id: String) {
        val userId = firebaseAuth.currentUser?.uid
        val playlistRef = firestore.collection("playlistTracks")
        val query = playlistRef.whereEqualTo("userId",userId).whereEqualTo("id",id).get().await()

        for(document in query.documents ){
            playlistRef.document(document.id).delete()
        }
    }
}