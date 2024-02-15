package com.example.spotifyclone.ui.fragments.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.model.dto.PlaylistModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserLibraryViewModel(private val roomDB: RoomDB) : ViewModel() {
    private val _playlists = MutableLiveData<List<PlaylistModel>>()

    val playlists: LiveData<List<PlaylistModel>>
        get() = _playlists

    fun getPlaylists() {
        val playlistDao = roomDB.playlistDao()
        viewModelScope.launch(Dispatchers.IO) {
            val playlistDb = playlistDao.getAll()
            val playlistModel = playlistDb.map {
                PlaylistModel(it.id, it.playlistName)
            }
            _playlists.postValue(playlistModel)

        }
    }
}