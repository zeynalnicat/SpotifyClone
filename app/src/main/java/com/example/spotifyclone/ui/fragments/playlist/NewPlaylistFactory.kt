package com.example.spotifyclone.ui.fragments.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.db.RoomDB

class NewPlaylistFactory(val roomDB: RoomDB):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewPlaylistViewModel(roomDB) as T

    }
}