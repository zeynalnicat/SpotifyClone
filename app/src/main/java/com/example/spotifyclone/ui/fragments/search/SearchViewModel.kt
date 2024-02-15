package com.example.spotifyclone.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.categories.Item
import com.example.spotifyclone.network.retrofit.api.CategoriesApi
import kotlinx.coroutines.launch

class SearchViewModel(private val categoriesApi: CategoriesApi) : ViewModel() {
    private val _categories = MutableLiveData<List<Item>>()

    val categories: LiveData<List<Item>>
        get() = _categories


    fun getCategories() {
        viewModelScope.launch {
            val response = categoriesApi.getCategories()
            if (response.isSuccessful) {
                _categories.postValue(response.body()?.categories?.items)
            }
        }
    }
}