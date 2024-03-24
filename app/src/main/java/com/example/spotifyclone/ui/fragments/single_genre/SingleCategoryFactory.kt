package com.example.spotifyclone.ui.fragments.single_genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.data.network.api.deezer.TrackApi

class SingleCategoryFactory(private val trackApi: TrackApi):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SingleCategoryViewModel(trackApi) as T
    }
}