package com.example.spotifyclone.ui.fragments.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.data.network.api.AlbumApi
import com.example.spotifyclone.domain.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val albumApi: AlbumApi,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _album = MutableLiveData<com.example.spotifyclone.domain.model.album.singlealbum.Album>()
    private val _insertion = MutableLiveData<Resource<Long>>()
    private val _isInDB = MutableLiveData(false)

    private val _insertionLiked = MutableLiveData<Long>()

    private val _isInLiked = MutableLiveData(false)

    val album: LiveData<com.example.spotifyclone.domain.model.album.singlealbum.Album>
        get() = _album


    val isInDB: LiveData<Boolean>
        get() = _isInDB


    val insertionLiked: LiveData<Long> get() = _insertionLiked

    val isInLiked :LiveData<Boolean> get()=_isInLiked


    fun getAlbum(id: String) {
        viewModelScope.launch {
            val response = albumApi.getAlbum(id)
            if (response.isSuccessful) {
                _album.postValue(response.body())
            }
        }
    }

    fun saveDB(albumId: String, isFirebase: Boolean,isDeezer:Boolean = false) {
        val userId = firebaseAuth.currentUser?.uid
        if (isFirebase) {

            if(isDeezer){
                val albumRef = firestore.collection("deezerAlbum")
                val query = albumRef.whereEqualTo("userId", userId).whereEqualTo("albumId", albumId)
                addFirebaseAlbum(query, albumRef, albumId, userId.toString())
            }else{
                val albumRef = firestore.collection("firebaseAlbum")
                val query = albumRef.whereEqualTo("userId", userId).whereEqualTo("albumId", albumId)
                addFirebaseAlbum(query, albumRef, albumId, userId.toString())
            }

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


    fun checkInDB(albumId: String, isFirebase: Boolean,isDeezer: Boolean=false) {
        val userId = firebaseAuth.currentUser?.uid
        if (isFirebase) {
            if(isDeezer){
                val albumRef = firestore.collection("deezerAlbum")
                val query = albumRef.whereEqualTo("userId", userId).whereEqualTo("albumId", albumId)
                checkFirestore(query)
            }else{
                val albumRef = firestore.collection("firebaseAlbum")
                val query = albumRef.whereEqualTo("userId", userId).whereEqualTo("albumId", albumId)
                checkFirestore(query)
            }

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

    fun checkLikedSongs(musicName:String){
        val likedSongRef = firestore.collection("likedSongs")
        val userId = firebaseAuth.currentUser?.uid
        val query = likedSongRef.whereEqualTo("userId",userId).whereEqualTo("name",musicName)

        query.get()
            .addOnCompleteListener {task->

                if(task.isSuccessful){
                    val result = task.result
                    if(result!=null && !result.isEmpty){
                        _isInLiked.postValue(true)
                    }else{
                        _isInLiked.postValue(false)
                    }
                }else{
                    _isInLiked.postValue(false)
                }

            }

    }

}