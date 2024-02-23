package com.example.spotifyclone.ui.fragments.single_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.model.firebase.Albums
import com.example.spotifyclone.model.firebase.Tracks
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SinglePlaylistViewModel(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _playlistName = MutableLiveData<Resource<String>>()


    private val _tracks = MutableLiveData<Resource<List<LikedSongs>>>()
    val playlistName: LiveData<Resource<String>> get() = _playlistName


    val tracks: LiveData<Resource<List<LikedSongs>>> get() = _tracks


    fun getPlaylistName(id: String) {
        val userId = firebaseAuth.currentUser?.uid
        val playlistRef = firestore.collection("playlists")
        val query = playlistRef.whereEqualTo("userId", userId).whereEqualTo("id", id)

        query.get().addOnSuccessListener {
            if (!it.isEmpty && it != null) {
                val result = it.documents[0]
                _playlistName.postValue(Resource.Success(result["name"].toString()))
            } else {
                _playlistName.postValue(Resource.Error(Exception("Error")))
            }
        }.addOnFailureListener { e ->
            _playlistName.postValue(Resource.Error(e))
        }

    }

    fun getTracks(id: String) {
        val userId = firebaseAuth.currentUser?.uid

        viewModelScope.launch(Dispatchers.IO) {
            val trackList = mutableListOf<LikedSongs>()
            val trackIds = getTracksIds(id)
            val database = FirebaseDatabase.getInstance()
            val refAlbums = database.getReference("albums")

            refAlbums.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    for (v in ds.children) {
                        val albumMap = v.value as HashMap<*, *>
                        val tracksMap = albumMap["tracks"] as List<Map<*, *>>

                        tracksMap.forEach {
                            if (it.contains(trackIds.any())) {
                                trackList.add(
                                    LikedSongs(
                                        it.get("name").toString(),
                                        it.get("artist").toString(),
                                        "",
                                        it.get("trackUri").toString()
                                    )
                                )
                            }
                        }

                    }
                    _tracks.postValue(Resource.Success(trackList))

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }
    }


    suspend fun getTracksIds(id:String): List<String> {
        val userId = firebaseAuth.currentUser?.uid
        val playlistRef = firestore.collection("playlistTracks")
        val query = playlistRef.whereEqualTo("userId", userId).whereEqualTo("playlistId",id).get().await()
        val tracksIds = mutableListOf<String>()
        if (query != null && !query.isEmpty) {
            val documents = query.documents
            for (document in documents) {
                tracksIds.add(document["trackId"].toString())
            }
        }
        return tracksIds
    }
}