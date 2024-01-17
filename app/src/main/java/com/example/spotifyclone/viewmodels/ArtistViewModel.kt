package com.example.spotifyclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.retrofit.RetrofitInstance
import com.example.spotifyclone.retrofit.api.ArtistsApi
import kotlinx.coroutines.launch
import retrofit2.create

class ArtistViewModel : ViewModel() {
    private val _albums = MutableLiveData<List<Item>>()
    private val artistApi = RetrofitInstance.getInstance()?.create(ArtistsApi::class.java)!!

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