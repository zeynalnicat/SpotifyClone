package com.example.spotifyclone.ui.fragments.home.music.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.data.network.api.deezer.TrackApi

class HomeMusicFactory(private val trackApi: TrackApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeMusicViewModel(trackApi) as T
    }
}