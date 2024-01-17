package com.example.spotifyclone.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.viewmodels.NewPlaylistViewModel

class NewPlaylistFactory(val roomDB: RoomDB):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewPlaylistViewModel(roomDB) as T

    }
}