package com.example.spotifyclone.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.dto.SearchModel
import com.example.spotifyclone.network.deezer.SearchApi
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchNextViewModel(private val searchApi: SearchApi, private val firestore: FirebaseFirestore,private val firebaseAuth: FirebaseAuth) : ViewModel() {
    private val _searchResults = MutableLiveData<Resource<List<SearchModel>>>()

    val searchResults: LiveData<Resource<List<SearchModel>>> get() = _searchResults

    private val _isInLiked = MutableLiveData<Boolean>(false)

    private val _insertionLiked = MutableLiveData<Long>()


    val isInLiked: LiveData<Boolean> get() = _isInLiked
    fun search(query: String) {
        _searchResults.postValue(Resource.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = searchApi.search(query)
                if (response.isSuccessful) {
                    val result = response.body()?.data
                    result?.let { data ->
                        val model = data.map {
                            SearchModel(
                                it.title,
                                it.artist.name,
                                it.album.cover_medium,
                                it.preview
                            )
                        }
                        _searchResults.postValue(Resource.Success(model))
                    }
                }
            } catch (e: Exception) {
                _searchResults.postValue(Resource.Error(e))
            }
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
}