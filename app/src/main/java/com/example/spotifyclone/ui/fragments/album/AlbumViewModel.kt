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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.Exception

class AlbumViewModel(private val roomDB: RoomDB,private val albumApi:AlbumApi) : ViewModel() {
    private val _album = MutableLiveData<Album>()
    private val _insertion = MutableLiveData<Resource<Long>>()
    private val _isInDB = MutableLiveData<Boolean>(false)

    private val _insertionLiked = MutableLiveData<Long>()

    val album: LiveData<Album>
        get() = _album

    val insertion: LiveData<Resource<Long>>
        get() = _insertion

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

    fun saveDB(albumId: String) {
        val albumDao = roomDB.albumDao()
        checkInDB(albumId)
        if (_isInDB.value == true) {
            _insertion.postValue(Resource.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    albumDao.delete(albumId)
                    _insertion.postValue(Resource.Success(0L))
                    _isInDB.postValue(false)
                } catch (e: Exception) {
                    _insertion.postValue(Resource.Error(e))
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val insert = albumDao.insert(AlbumEntity(albumId = albumId))
                    if (insert != -1L) {
                        _insertion.postValue(Resource.Success(insert))
                        _isInDB.postValue(true)
                    } else {
                        _insertion.postValue(Resource.Error(Exception("There was an error while handling the request")))
                    }
                } catch (e: Exception) {
                    _insertion.postValue(Resource.Error(e))
                }
            }
        }
    }


    fun checkInDB(albumId: String) {
        val albumDao = roomDB.albumDao()
        viewModelScope.launch(Dispatchers.IO) {
            val count = albumDao.checkInDB(albumId)
            _isInDB.postValue(count > 0)

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