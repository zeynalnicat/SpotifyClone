package com.example.spotifyclone.ui.fragments.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.network.db.album.AlbumEntity
import com.example.spotifyclone.network.db.likedsongs.LikedSongsEntity
import com.example.spotifyclone.model.album.singlealbum.Album
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.Exception

class AlbumViewModel(
    private val roomDB: RoomDB,
    private val albumApi: AlbumApi,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _album = MutableLiveData<Album>()
    private val _insertion = MutableLiveData<Resource<Long>>()
    private val _isInDB = MutableLiveData<Boolean>(false)

    private val _insertionLiked = MutableLiveData<Long>()

    val album: LiveData<Album>
        get() = _album


    val isInDB: LiveData<Boolean>
        get() = _isInDB


    val insertionLiked: LiveData<Long> get() = _insertionLiked


    fun getAlbum(id: String) {
        viewModelScope.launch {
            val response = albumApi.getAlbum(id)
            if (response.isSuccessful) {
                _album.postValue(response.body())
            }
        }
    }

    fun saveDB(albumId: String, isFirebase: Boolean) {
        val userId = firebaseAuth.currentUser?.uid
        if (isFirebase) {
            val albumRef = firestore.collection("firebaseAlbum")
            val query = albumRef.whereEqualTo("userId", userId).whereEqualTo("albumId", albumId)
            addFirebaseAlbum(query, albumRef, albumId, userId.toString())
        } else {
            val albumRef = firestore.collection("retrofitAlbum")
            val query = albumRef.whereEqualTo("userId", userId).whereEqualTo("albumId", albumId)
            addFirebaseAlbum(query, albumRef, albumId, userId.toString())

        }
    }

    fun addFirebaseAlbum(
        query: Query,
        albumRef: CollectionReference,
        albumId: String,
        userId: String
    ) {

        try {
            query.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val existingDocument = task.result
                        if (existingDocument != null && !existingDocument.isEmpty) {
                            val document = existingDocument.documents[0].id
                            albumRef.document(document).delete()
                            _isInDB.postValue(false)
                            _insertion.postValue(Resource.Success(0L))
                        } else {
                            val album = hashMapOf(
                                "albumId" to albumId,
                                "userId" to userId
                            )
                            albumRef.add(album)
                            _insertion.postValue(Resource.Success(3))
                            _isInDB.postValue(true)
                        }
                    }

                }
        } catch (e: Exception) {
            _insertion.postValue(Resource.Error(e))
        }
    }


    fun checkInDB(albumId: String, isFirebase: Boolean) {
        val userId = firebaseAuth.currentUser?.uid
        if (isFirebase) {
            val albumRef = firestore.collection("firebaseAlbum")
            val query = albumRef.whereEqualTo("userId", userId).whereEqualTo("albumId", albumId)
            checkFirestore(query)
        } else {
            val albumRef = firestore.collection("retrofitAlbum")
            val query = albumRef.whereEqualTo("userId", userId).whereEqualTo("albumId", albumId)
            checkFirestore(query)

        }
    }

    fun checkFirestore(query: Query) {
        try {
            query.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && !document.isEmpty) {
                            _isInDB.postValue(true)
                        } else {
                            _isInDB.postValue(false)
                        }
                    }
                }
        } catch (e: Exception) {

        }
    }

    fun insertLikedSongs(name: String, artist: String, img: String, uri: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val likedSongsDao = roomDB.likedSongsDao()
                val check = likedSongsDao.insert(LikedSongsEntity(0, name, artist, img, uri))
                _insertionLiked.postValue(check)

            } catch (e: Exception) {
                _insertionLiked.postValue(-1L)
            }

        }

    }

}