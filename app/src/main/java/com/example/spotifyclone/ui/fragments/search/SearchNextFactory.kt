package com.example.spotifyclone.ui.fragments.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.deezer.SearchApi

class SearchNextFactory(private val searchApi: SearchApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchNextViewModel(searchApi) as T
    }
}