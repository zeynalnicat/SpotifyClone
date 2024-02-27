package com.example.spotifyclone.ui.fragments.home.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.deezer.TrackApi

class HomeMusicFactory(private val trackApi: TrackApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeMusicViewModel(trackApi) as T
    }
}