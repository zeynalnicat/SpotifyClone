package com.example.spotifyclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.model.album.popularalbums.Album
import com.example.spotifyclone.retrofit.RetrofitInstance
import com.example.spotifyclone.retrofit.api.AlbumApi
import com.example.spotifyclone.retrofit.api.ArtistsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {

    private val _likedAlbums = MutableLiveData<List<Album>>()
    private val _roomAlbums = MutableLiveData<List<String>>()
    private val albumApi =  RetrofitInstance.getInstance()?.create(AlbumApi::class.java)!!

    val likedAlbums: LiveData<List<Album>>
        get() = _likedAlbums

    val roomAlbums:LiveData<List<String>>
        get() = _roomAlbums

    fun getFromDB(roomDB: RoomDB) {
        val albumDao = roomDB.albumDao()
        viewModelScope.launch(Dispatchers.IO) {
            val listAlbums = albumDao.getAll()
            _roomAlbums.postValue(listAlbums)
        }
    }

    fun getAlbumsFromApi(albumIDs:List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val query = albumIDs.joinToString(",")

                val response = albumApi.getSomeAlbums(query)
                if (response.isSuccessful) {
                    _likedAlbums.postValue(response.body()?.albums)
                }

        }
    }
}