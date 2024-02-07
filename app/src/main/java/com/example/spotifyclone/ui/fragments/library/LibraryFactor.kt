package com.example.spotifyclone.ui.fragments.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.db.RoomDB

class LibraryFactor(private val roomDB: RoomDB):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(roomDB) as T
    }
}