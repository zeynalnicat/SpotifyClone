package com.example.spotifyclone.ui.fragments.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.model.album.popularalbums.Album
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibraryViewModel(private val roomDB: RoomDB, private val albumApi: AlbumApi) : ViewModel() {

    private val _likedAlbums = MutableLiveData<List<Album>>()
    private val _roomAlbums = MutableLiveData<List<String>>()
    private val _count = MutableLiveData<Int>(0)
    private val likedSongsDao = roomDB.likedSongsDao()


    val count: LiveData<Int> get() = _count

    val likedAlbums: LiveData<List<Album>>
        get() = _likedAlbums

    val roomAlbums: LiveData<List<String>>
        get() = _roomAlbums

    fun getFromDB() {
        val albumDao = roomDB.albumDao()
        viewModelScope.launch(Dispatchers.IO) {
            val listAlbums = albumDao.getAll()
            _roomAlbums.postValue(listAlbums)
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