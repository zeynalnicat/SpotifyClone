package com.example.spotifyclone.ui.fragments.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.db.RoomDB

class AlbumFactory(private val roomDB: RoomDB) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlbumViewModel(roomDB) as T
    }
}