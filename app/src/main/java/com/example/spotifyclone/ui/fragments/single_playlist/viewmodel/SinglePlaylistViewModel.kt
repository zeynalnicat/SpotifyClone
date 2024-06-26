package com.example.spotifyclone.ui.fragments.single_playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyclone.domain.model.dto.LikedSongs
import com.example.spotifyclone.domain.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SinglePlaylistViewModel(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _playlistName = MutableLiveData<Resource<String>>()


    private val _tracks = MutableLiveData<Resource<List<LikedSongs>>>()

    private val _isInLiked = MutableLiveData<Boolean>(false)

    private val _insertionLiked = MutableLiveData<Long>()

    val playlistName: LiveData<Resource<String>> get() = _playlistName


    val tracks: LiveData<Resource<List<LikedSongs>>> get() = _tracks

    val isInLiked: LiveData<Boolean> get() = _isInLiked

    private var trackModels: List<LikedSongs> = emptyList()

    val insertionLiked: LiveData<Long> get() = _insertionLiked

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

        val playlistRef = firestore.collection("playlistTracks")
        val query = playlistRef.whereEqualTo("userId", userId).whereEqualTo("playlistId", id)
        try {
            query.get().addOnSuccessListener { querySnapshot ->
                val trackList = mutableListOf<LikedSongs>()
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val documents = querySnapshot.documents
                    for (document in documents) {
                        val musicModel = LikedSongs(
                            document["trackName"].toString(),
                            document["artist"].toString(),
                            document["imgUri"].toString(),
                            document["trackUri"].toString(),

                            )
                        trackList.add(musicModel)
                    }
                    _tracks.postValue(Resource.Success(trackList))
                    trackModels = trackList
                } else {
                    _tracks.postValue(Resource.Error(Exception("No tracks")))
                }
            }
        } catch (e: Exception) {
            _tracks.postValue(Resource.Error(e))
        }
    }


    fun checkLikedSongs(musicName: String) {
        val likedSongRef = firestore.collection("likedSongs")
        val userId = firebaseAuth.currentUser?.uid
        val query = likedSongRef.whereEqualTo("userId", userId).whereEqualTo("name", musicName)

        query.get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null && !result.isEmpty) {
                        _isInLiked.postValue(true)
                    } else {
                        _isInLiked.postValue(false)
                    }
                } else {
                    _isInLiked.postValue(false)
                }

            }

    }

    fun insertLikedSongs(name: String, artist: String, img: String, uri: String) {
        val likedSongRef = firestore.collection("likedSongs")
        val userId = firebaseAuth.currentUser?.uid
        val query = likedSongRef.whereEqualTo("userId", userId).whereEqualTo("musicUri", uri)

        try {
            query.get()
                .addOnCompleteListener { task ->
                    val result = task.result
                    if (result != null && !result.isEmpty) {
                        val document = result.documents[0]
                        document.reference.delete()
                        _insertionLiked.postValue(0)
                        _isInLiked.postValue(false)
                    } else {
                        val music = hashMapOf(
                            "artist" to artist,
                            "imgUri" to img,
                            "musicUri" to uri,
                            "name" to name,
                            "userId" to userId
                        )
                        likedSongRef.add(music)
                            .addOnSuccessListener {
                                _insertionLiked.postValue(2)
                                _isInLiked.postValue(true)
                            }
                            .addOnFailureListener {
                                _insertionLiked.postValue(-1L)
                            }

                    }
                }

        } catch (e: Exception) {
            _insertionLiked.postValue(-1L)

        }
    }


    fun removeFromPlaylist(playlistId: String, musicName: String) {
        val userId = firebaseAuth.currentUser?.uid
        val playlistRef = firestore.collection("playlistTracks")
        val query =
            playlistRef.whereEqualTo("userId", userId).whereEqualTo("playlistId", playlistId)
                .whereEqualTo("trackName", musicName)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val document = querySnapshot.documents[0]
                playlistRef.document(document.id).delete()
            }

    }


    fun search(text: String) {
        val model = _tracks.value
        if (model is Resource.Success) {
            if (text.isEmpty()) {
                _tracks.postValue(Resource.Success(trackModels))
            } else {
                val searched = trackModels.filter { it.name.contains(text, ignoreCase = true) }
                _tracks.postValue(Resource.Success(searched))
            }
        }

    }


}