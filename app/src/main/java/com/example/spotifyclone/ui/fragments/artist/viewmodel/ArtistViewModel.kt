package com.example.spotifyclone.ui.fragments.artist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.data.network.api.ArtistsApi
import com.example.spotifyclone.domain.model.album.newrelease.Item
import kotlinx.coroutines.launch

class ArtistViewModel(private val artistApi: ArtistsApi) : ViewModel() {
    private val _albums = MutableLiveData<List<Item>>()

    val albums: LiveData<List<Item>>
        get() = _albums


    fun getAlbums(id: String) {
        viewModelScope.launch {
            val response =artistApi.getArtistAlbums(id)
            if(response.isSuccessful){
                _albums.postValue(response.body()?.items)
            }
        }
    }
}