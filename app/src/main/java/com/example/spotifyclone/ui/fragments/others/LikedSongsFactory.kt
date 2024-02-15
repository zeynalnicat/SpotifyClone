package com.example.spotifyclone.ui.fragments.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.db.RoomDB

class LikedSongsFactory(private val roomDB: RoomDB) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LikedSongsViewModel(roomDB) as T
    }
}