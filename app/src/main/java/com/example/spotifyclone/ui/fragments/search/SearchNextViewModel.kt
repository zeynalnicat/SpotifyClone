package com.example.spotifyclone.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.dto.SearchModel
import com.example.spotifyclone.network.deezer.SearchApi
import com.example.spotifyclone.resource.Resource
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchNextViewModel(private val searchApi: SearchApi) : ViewModel() {
    private val _searchResults = MutableLiveData<Resource<List<SearchModel>>>()

    val searchResults: LiveData<Resource<List<SearchModel>>> get() = _searchResults


    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = searchApi.search(query)
                if (response.isSuccessful) {
                    val result = response.body()?.data
                    result?.let { data ->
                        val model = data.map {
                            SearchModel(
                                it.title,
                                it.artist.name,
                                it.album.cover,
                                it.preview
                            )
                        }
                        _searchResults.postValue(Resource.Success(model))
                    }
                }
            } catch (e: Exception) {
                _searchResults.postValue(Resource.Error(e))
            }
        }
    }
}