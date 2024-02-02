package com.example.spotifyclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.pseudo_models.PlaylistModel
import com.example.spotifyclone.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPlaylistViewModel(private val roomDB: RoomDB) : ViewModel() {

    private val _playlists = MutableLiveData<Resource<List<PlaylistModel>>>()

    val playlists: LiveData<Resource<List<PlaylistModel>>>
        get() = _playlists


    fun getPlaylists() {
        val playlistDao = roomDB.playlistDao()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lists = playlistDao.getAll()
                if (lists.isNotEmpty()) {
                    val playlistModel = lists.map {
                        PlaylistModel(
                            it.id,
                            it.playlistName,
                            it.playListImg,
                            0,
                            false
                        )
                    }
                    _playlists.postValue(Resource.Success(playlistModel))
                } else {
                    _playlists.postValue(Resource.Error(Exception("There is no data from playlist")))
                }
            } catch (e: Exception) {
                _playlists.postValue(Resource.Error(e))
            }
        }
    }

}