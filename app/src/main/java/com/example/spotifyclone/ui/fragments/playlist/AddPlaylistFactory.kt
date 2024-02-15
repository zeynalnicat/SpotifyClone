package com.example.spotifyclone.ui.fragments.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.db.RoomDB

class AddPlaylistFactory(private val roomDB: RoomDB):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddPlaylistViewModel(roomDB) as T
    }
}