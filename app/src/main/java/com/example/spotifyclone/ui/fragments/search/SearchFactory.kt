package com.example.spotifyclone.ui.fragments.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyclone.network.retrofit.api.CategoriesApi

class SearchFactory(private val categoriesApi: CategoriesApi):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(categoriesApi) as T
    }
}