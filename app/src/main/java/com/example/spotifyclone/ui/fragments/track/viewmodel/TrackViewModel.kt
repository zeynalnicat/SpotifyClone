package com.example.spotifyclone.ui.fragments.track.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyclone.data.sp.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TrackViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {


    private val _insertionLiked = MutableLiveData<Long>()

    private val _isInLiked = MutableLiveData<Boolean>(false)

    val insertionLiked: LiveData<Long> get() = _insertionLiked

    val isInLiked: LiveData<Boolean> get() = _isInLiked


    private val _currentTrack = MutableLiveData<com.example.spotifyclone.domain.model.dto.MusicItem>()

    val currentTrack: LiveData<com.example.spotifyclone.domain.model.dto.MusicItem> get() = _currentTrack

    fun getCurrentTrack(sp: SharedPreference) {
        val name = sp.getValue("PlayingMusic", "")
        val artist = sp.getValue("PlayingMusicArtist", "")
        val uri = sp.getValue("PlayingMusicUri", "")

        _currentTrack.value =
            com.example.spotifyclone.domain.model.dto.MusicItem(artist, "", name, uri)

    }

    fun setCurrentTrack(musicItem: com.example.spotifyclone.domain.model.dto.MusicItem) {
        _currentTrack.value = musicItem
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

}