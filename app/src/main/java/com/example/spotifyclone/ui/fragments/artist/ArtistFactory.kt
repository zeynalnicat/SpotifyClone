package com.example.spotifyclone.ui.fragments.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.data.network.api.ArtistsApi

class ArtistFactory(private val artistApi: ArtistsApi) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArtistViewModel(artistApi) as T
    }
}