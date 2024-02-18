package com.example.spotifyclone.ui.fragments.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.spotifyclone.model.album.popularalbums.Album
import com.example.spotifyclone.model.firebase.Albums
import com.example.spotifyclone.model.firebase.Tracks
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val albumApi: AlbumApi,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _likedAlbums = MutableLiveData<List<Album>>()
    private val _albumIds = MutableLiveData<List<String>>()
    private val _count = MutableLiveData<Int>(0)

    private val _likedAlbumsFirestore =
        MutableLiveData<Resource<List<com.example.spotifyclone.model.dto.Album>>>()


    val count: LiveData<Int> get() = _count

    val likedAlbums: LiveData<List<Album>>
        get() = _likedAlbums

    val albumIds: LiveData<List<String>>
        get() = _albumIds

    val likedAlbumsFirestore: LiveData<Resource<List<com.example.spotifyclone.model.dto.Album>>> get() = _likedAlbumsFirestore

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

    fun getAlbumFirestore() {
        try {
            val userId = firebaseAuth.currentUser?.uid
            val database = FirebaseDatabase.getInstance()
            val albumRef = firestore.collection("firebaseAlbum")
            val query = albumRef.whereEqualTo("userId", userId)
            val refAlbums = database.getReference("albums")

            val listAlbums = mutableListOf<Albums>()
            val listAlbumIds = mutableListOf<String>()

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    if (!documents.isEmpty && documents != null) {
                        for (document in documents) {
                            listAlbumIds.add(document["albumId"] as String)
                        }
                        listAlbumIds.forEach {
                            refAlbums.orderByChild("id").equalTo(it).get()
                                .addOnSuccessListener { snapshot->
                                    for(ds in snapshot.children){
                                        val albumMap = ds.value as HashMap<*, *>
                                        val tracksMap = albumMap["tracks"] as List<Map<*, *>>
                                        val trackList = tracksMap.map { track ->
                                            val trackMap = track
                                            Tracks(
                                                artist = trackMap["artist"] as String?,
                                                id = trackMap["id"] as String?,
                                                name = trackMap["name"] as String?,
                                                trackUri = trackMap["trackUri"] as String?
                                            )
                                        }

                                        val album = Albums(
                                            albumMap["coverImg"] as String?,
                                            albumMap["id"] as String?,
                                            albumMap["name"] as String?,
                                            trackList
                                        )

                                        listAlbums.add(album)
                                    }
                                    val albumModel = listAlbums.map {
                                        com.example.spotifyclone.model.dto.Album(
                                            it.coverImg ?: "",
                                            it.id ?: "",
                                            it.name ?: "",
                                            it.tracks ?: emptyList(),
                                            true
                                        )
                                    }
                                    _likedAlbumsFirestore.postValue(Resource.Success(albumModel))

                                }


                        }
                    }
                }
            }

        } catch (e: Exception) {
            _likedAlbumsFirestore.postValue(Resource.Error(e))
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
        val userId = firebaseAuth.currentUser?.uid
        val likedSongsRef = firestore.collection("likedSongs")
        val query = likedSongsRef.whereEqualTo("userId",userId)

        try {
            query.get()
                .addOnSuccessListener {
                    val size = it.size()
                    _count.postValue(size)
                }
                .addOnFailureListener {
                    val size = 0
                    _count.postValue(0)
                }
        }catch (e:Exception){
               _count.postValue(0)
        }
    }
}