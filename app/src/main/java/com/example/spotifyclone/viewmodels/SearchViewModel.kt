package com.example.spotifyclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifyclone.model.categories.Item
import com.example.spotifyclone.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class SearchViewModel:ViewModel() {
    private val _categories = MutableLiveData<List<Item>>()
    private val categoryApi = RetrofitInstance.categoryApi.value

    val categories:LiveData<List<Item>>
        get() = _categories


    fun getCategories(){
        viewModelScope.launch {
            val response = categoryApi.getCategories()
            if(response.isSuccessful){
                _categories.postValue(response.body()?.categories?.items)
            }
        }
    }
}