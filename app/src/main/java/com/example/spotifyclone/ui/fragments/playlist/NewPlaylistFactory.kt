package com.example.spotifyclone.ui.fragments.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class NewPlaylistFactory():ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewPlaylistViewModel() as T

    }
}