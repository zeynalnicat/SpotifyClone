package com.example.spotifyclone.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.viewmodels.AlbumViewModel

class AlbumFactory(private val roomDB: RoomDB) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlbumViewModel(roomDB) as T
    }
}