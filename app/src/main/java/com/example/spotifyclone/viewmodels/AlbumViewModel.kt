package com.example.spotifyclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.db.album.AlbumEntity
import com.example.spotifyclone.model.album.singlealbum.Album
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.retrofit.RetrofitInstance
import com.example.spotifyclone.retrofit.api.AlbumApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create
import kotlin.Exception

class AlbumViewModel : ViewModel() {
    private val _album = MutableLiveData<Album>()
    private val albumApi = RetrofitInstance.getInstance()?.create(AlbumApi::class.java)!!
    private val _insertion = MutableLiveData<Resource<Long>>()
    private val _isInDB = MutableLiveData<Boolean>(false)

    val album: LiveData<Album>
        get() = _album

    val insertion: LiveData<Resource<Long>>
        get() = _insertion

    val isInDB: LiveData<Boolean>
        get() = _isInDB


    fun getAlbum(id: String) {
        viewModelScope.launch {
            val response = albumApi.getAlbum(id)
            if (response.isSuccessful) {
                _album.postValue(response.body())
            }
        }
    }

    fun saveDB(roomDB: RoomDB, albumId: String) {
        val albumDao = roomDB.albumDao()
        checkInDB(roomDB, albumId)
        if(_isInDB.value==true){
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
        }
        else {
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


    fun checkInDB(roomDB: RoomDB, albumId: String) {
        val albumDao = roomDB.albumDao()
        viewModelScope.launch(Dispatchers.IO) {
            val count = albumDao.checkInDB(albumId)
            _isInDB.postValue(count > 0)

        }
    }

}