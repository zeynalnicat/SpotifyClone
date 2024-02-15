package com.example.spotifyclone.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.network.retrofit.api.ArtistsApi

class HomeFactory(
    private val roomDB: RoomDB, private val albumApi: AlbumApi,
    private val artistApi: ArtistsApi
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(roomDB, albumApi, artistApi) as T
    }
}