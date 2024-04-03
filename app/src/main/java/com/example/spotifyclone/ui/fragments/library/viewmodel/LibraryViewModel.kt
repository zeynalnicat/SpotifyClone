package com.example.spotifyclone.ui.fragments.library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.data.network.api.AlbumApi
import com.example.spotifyclone.domain.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LibraryViewModel(
    private val albumApi: AlbumApi,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val deezerApi: com.example.spotifyclone.data.network.api.deezer.AlbumApi
) : ViewModel() {

    private val _likedAlbums = MutableLiveData<List<com.example.spotifyclone.domain.model.album.popularalbums.Album>>()
    private val _deezerAlbums =
        MutableLiveData<Resource<List<com.example.spotifyclone.domain.model.dto.Album>>>()
    private val _albumIds = MutableLiveData<List<String>>()
    private val _count = MutableLiveData<Int>(0)

    private val _likedAlbumsFirestore =
        MutableLiveData<Resource<List<com.example.spotifyclone.domain.model.dto.Album>>>()


    val count: LiveData<Int> get() = _count

    val likedAlbums: LiveData<List<com.example.spotifyclone.domain.model.album.popularalbums.Album>>
        get() = _likedAlbums

    val albumIds: LiveData<List<String>>
        get() = _albumIds

    val deezerAlbums: LiveData<Resource<List<com.example.spotifyclone.domain.model.dto.Album>>>
        get() = _deezerAlbums

    val likedAlbumsFirestore: LiveData<Resource<List<com.example.spotifyclone.domain.model.dto.Album>>> get() = _likedAlbumsFirestore

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

            val listAlbums = mutableListOf<com.example.spotifyclone.domain.model.firebase.Albums>()
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
                                .addOnSuccessListener { snapshot ->
                                    for (ds in snapshot.children) {
                                        val albumMap = ds.value as HashMap<*, *>
                                        val tracksMap = albumMap["tracks"] as List<Map<*, *>>
                                        val trackList = tracksMap.map { track ->
                                            val trackMap = track
                                            com.example.spotifyclone.domain.model.firebase.Tracks(
                                                artist = trackMap["artist"] as String?,
                                                id = trackMap["id"] as String?,
                                                name = trackMap["name"] as String?,
                                                trackUri = trackMap["trackUri"] as String?
                                            )
                                        }

                                        val album =
                                            com.example.spotifyclone.domain.model.firebase.Albums(
                                                albumMap["coverImg"] as String?,
                                                albumMap["id"] as String?,
                                                albumMap["name"] as String?,
                                                trackList
                                            )

                                        listAlbums.add(album)
                                    }
                                    val albumModel = listAlbums.map {
                                        com.example.spotifyclone.domain.model.dto.Album(
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

    fun getDeezerAlbum() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ids = getDeezerIds()
                val listAlbum = mutableListOf<com.example.spotifyclone.domain.model.dto.Album>()
                for (id in ids) {
                    val response = deezerApi.getAlbum(id)
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            val model = com.example.spotifyclone.domain.model.dto.Album(
                                it.cover_xl,
                                "" + it.id,
                                it.title,
                                it.tracks.data.map { track ->
                                    com.example.spotifyclone.domain.model.firebase.Tracks(
                                        track.artist.name,
                                        "",
                                        track.title,
                                        track.preview,
                                    )
                                }, isFirebase = true
                            )
                            listAlbum.add(model)
                        }
                        _deezerAlbums.postValue(Resource.Success(listAlbum))
                    }
                }

            } catch (e: Exception) {
                _deezerAlbums.postValue(Resource.Error(e))
            }
        }

    }


    suspend fun getDeezerIds(): List<Int> {
        val userId = firebaseAuth.currentUser?.uid
        val albumRef = firestore.collection("deezerAlbum")
        val query = albumRef.whereEqualTo("userId", userId).get().await()
        var ids = mutableListOf<Int>()

        for (document in query.documents) {
            ids.add(document["albumId"].toString().toInt())
        }
        return ids

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
        val query = likedSongsRef.whereEqualTo("userId", userId)

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
        } catch (e: Exception) {
            _count.postValue(0)
        }
    }
}