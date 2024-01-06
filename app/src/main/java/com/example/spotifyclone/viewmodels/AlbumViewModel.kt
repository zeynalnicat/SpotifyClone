package com.example.spotifyclone.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.album.singlealbum.Album
import com.example.spotifyclone.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class AlbumViewModel : ViewModel() {
    private val _album = MutableLiveData<Album>()
    private val albumApi = RetrofitInstance.albumApi.value

    val album: LiveData<Album>
        get() = _album

    fun getAlbum(id:String){
        viewModelScope.launch {
           val response = albumApi.getAlbum(id)
            if(response.isSuccessful){
                _album.postValue(response.body())
            }
        }
    }
}