package com.example.spotifyclone.ui.fragments.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.network.retrofit.api.AlbumApi

class AlbumFactory(private val roomDB: RoomDB,private val albumApi:AlbumApi) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlbumViewModel(roomDB,albumApi) as T
    }
}