package com.example.spotifyclone.ui.fragments.others

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class LikedSongsViewModel(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _songs = MutableLiveData<Resource<List<LikedSongs>>>()


    val songs: LiveData<Resource<List<LikedSongs>>>
        get() = _songs


    fun getSongs() {
        val userId = firebaseAuth.currentUser?.uid
        val likedSongsRef = firestore.collection("likedSongs")
        val query = likedSongsRef.whereEqualTo("userId", userId)
        try {
            query.get()
                .addOnCompleteListener { task ->
                    val result = task.result
                    val listLikedSongs = mutableListOf<LikedSongs>()
                    if (result != null && !result.isEmpty) {
                        val documents = result.documents
                        for (document in documents) {
                            val song = LikedSongs(
                                document["name"].toString(),
                                document["artist"].toString(),
                                document["imgUri"].toString(),
                                document["musicUri"].toString()
                            )
                            listLikedSongs.add(song)
                        }
                        _songs.postValue(Resource.Success(listLikedSongs))
                    } else {
                        _songs.postValue(Resource.Error(Exception("There was an error")))
                    }

                }

        } catch (e: Exception) {
            _songs.postValue(Resource.Error(e))
        }

    }

    fun search(query: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            _songs.postValue(Resource.Error(Exception("User not authenticated")))
            return
        }

        val likedSongsRef = firestore.collection("likedSongs")
        val lowercaseQuery = query.toLowerCase()
        val query = likedSongsRef
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("name", lowercaseQuery)
            .whereLessThanOrEqualTo("name", lowercaseQuery + "\uf8ff")
            .orderBy("name", Query.Direction.ASCENDING)
            .orderBy("artist", Query.Direction.ASCENDING)


        query.get().addOnCompleteListener { task ->
            _songs.postValue(Resource.Loading)
            if (task.isSuccessful) {
                val result = task.result
                val listLikedSongs = mutableListOf<LikedSongs>()

                if (result != null && !result.isEmpty) {
                    val documents = result.documents
                    for (document in documents) {
                        val song = LikedSongs(
                            document["name"].toString(),
                            document["artist"].toString(),
                            document["imgUri"].toString(),
                            document["musicUri"].toString()
                        )
                        listLikedSongs.add(song)
                    }
                    _songs.postValue(Resource.Success(listLikedSongs))
                } else {
                    _songs.postValue(Resource.Error(Exception("No matching songs found")))
                }
            } else {
                _songs.postValue(Resource.Error(task.exception ?: Exception("Unknown error")))
            }
        }
    }

}