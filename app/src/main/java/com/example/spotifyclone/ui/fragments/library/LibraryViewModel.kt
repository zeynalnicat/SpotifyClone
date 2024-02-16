package com.example.spotifyclone.ui.fragments.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.model.album.popularalbums.Album
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val roomDB: RoomDB,
    private val albumApi: AlbumApi,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _likedAlbums = MutableLiveData<List<Album>>()
    private val _albumIds = MutableLiveData<List<String>>()
    private val _count = MutableLiveData<Int>(0)
    private val likedSongsDao = roomDB.likedSongsDao()


    val count: LiveData<Int> get() = _count

    val likedAlbums: LiveData<List<Album>>
        get() = _likedAlbums

    val albumIds: LiveData<List<String>>
        get() = _albumIds

    fun getFromDB() {
        val albumRef = firestore.collection("retrofitAlbum")
        val userId = firebaseAuth.currentUser?.uid
        val query = albumRef.whereEqualTo("userId", userId)
        val listAlbumIds = mutableListOf<String>()

        query.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (documents != null && !documents.isEmpty) {
                        for (document in documents) {
                            listAlbumIds.add(document["albumId"] as String)
                        }
                        _albumIds.postValue(listAlbumIds)
                    }

                }
            }

    }

    fun getAlbumsFromApi(albumIDs: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val query = albumIDs.joinToString(",")
            val response = albumApi.getSomeAlbums(query)
            if (response.isSuccessful) {
                _likedAlbums.postValue(response.body()?.albums)
            }

        }
    }

    fun setSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val countSongs = likedSongsDao.getSize()
            _count.postValue(countSongs)
        }
    }
}