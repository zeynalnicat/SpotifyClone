package com.example.spotifyclone.ui.fragments.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddPlaylistFactory():ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddPlaylistViewModel() as T
    }
}