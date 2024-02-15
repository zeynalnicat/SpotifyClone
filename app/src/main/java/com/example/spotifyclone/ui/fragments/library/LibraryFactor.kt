package com.example.spotifyclone.ui.fragments.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.network.retrofit.api.AlbumApi

class LibraryFactor(private val roomDB: RoomDB, private val albumApi: AlbumApi) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(roomDB, albumApi) as T
    }
}