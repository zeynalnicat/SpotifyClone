package com.example.spotifyclone.ui.fragments.track

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.sp.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrackViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {


    private val _insertionLiked = MutableLiveData<Long>()

    private val _isInLiked = MutableLiveData<Boolean>(false)

    val insertionLiked: LiveData<Long> get() = _insertionLiked

    val isInLiked: LiveData<Boolean> get() = _isInLiked


    private val _currentTrack = MutableLiveData<MusicItem>()

    val currentTrack: LiveData<MusicItem> get() = _currentTrack

    fun getCurrentTrack(sp: SharedPreference) {
        val name = sp.getValue("PlayingMusic", "")
        val artist = sp.getValue("PlayingMusicArtist", "")
        val uri = sp.getValue("PlayingMusicUri", "")

        _currentTrack.value = MusicItem(artist, "", name, uri)

    }

    fun setCurrentTrack(musicItem: MusicItem) {
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