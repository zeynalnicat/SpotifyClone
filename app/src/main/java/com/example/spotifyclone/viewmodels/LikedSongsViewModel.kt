package com.example.spotifyclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.db.likedsongs.LikedSongsEntity
import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LikedSongsViewModel(private val roomDB: RoomDB) : ViewModel() {

    private val _songs = MutableLiveData<Resource<List<LikedSongs>>>()
    private val likedSongsDao = roomDB.likedSongsDao()

    val songs: LiveData<Resource<List<LikedSongs>>>
        get() = _songs


    fun getSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val likedSongs = likedSongsDao.selectAll()
                if (likedSongs.isNotEmpty()) {
                    val model =
                        likedSongs.map { LikedSongs(it.id, it.name, it.artist, it.imgUri, it.uri) }

                    _songs.postValue(Resource.Success(model))
                }
            } catch (e: Exception) {
                _songs.postValue(Resource.Error(e))
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val likedSongs = likedSongsDao.search(query)
                if (likedSongs.isNotEmpty()) {
                    val model =
                        likedSongs.map { LikedSongs(it.id, it.name, it.artist, it.imgUri, it.uri) }

                    _songs.postValue(Resource.Success(model))
                }
            } catch (e: Exception) {
                _songs.postValue(Resource.Error(e))
            }
        }
    }
}