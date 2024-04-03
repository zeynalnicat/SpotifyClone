package com.example.spotifyclone.ui.fragments.choose_artist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.data.network.api.ArtistsApi

class ChooseArtistFactory(private val artistsApi: ArtistsApi):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChoseArtistViewModel(artistsApi) as T
    }
}