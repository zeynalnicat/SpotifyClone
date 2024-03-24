package com.example.spotifyclone.ui.fragments.others

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.spotifyclone.domain.model.dto.LikedSongs
import com.example.spotifyclone.domain.resource.Resource
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
                    val listLikedSongs = mutableListOf<com.example.spotifyclone.domain.model.dto.LikedSongs>()
                    if (result != null && !result.isEmpty) {
                        val documents = result.documents
                        for (document in documents) {
                            val song = com.example.spotifyclone.domain.model.dto.LikedSongs(
                                document["name"].toString(),
                                document["artist"].toString(),
                                document["imgUri"].toString(),
                                document["musicUri"].toString()
                            )
                            listLikedSongs.add(song)
                        }
                        _songs.postValue(Resource.Success(listLikedSongs))
                    }

                }

        } catch (e: Exception) {
            _songs.postValue(Resource.Error(e))
        }

    }

    fun search(query: String) {
        val model = _songs.value
        if (model is Resource.Success) {
            if (query.isEmpty()) {
                getSongs()
            } else {
                val searched = model.data.filter { it.name.contains(query, ignoreCase = true) }
                _songs.postValue(Resource.Success(searched))
            }
        }
    }


    fun removeLikedSongs(musicName: String) {
        val likedSongsRef = firestore.collection("likedSongs")
        val userId = firebaseAuth.currentUser?.uid
        val query = likedSongsRef.whereEqualTo("userId", userId).whereEqualTo("name", musicName)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    likedSongsRef.document(document.id).delete()
                    getSongs()
                }

            }

    }

}