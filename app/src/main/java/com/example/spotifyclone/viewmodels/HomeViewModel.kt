package com.example.spotifyclone.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.album.newrelease.AlbumsX
import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.model.album.trysomething.Album
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val _date = MutableLiveData<String>()
    private val _newReleases = MutableLiveData<List<Item>>()
    private val _trySomething = MutableLiveData<List<Album>>()
    private val _artists = MutableLiveData<List<Artist>>()
    private val albumApi = RetrofitInstance.albumApi.value
    private val artistApi = RetrofitInstance.artistsApi.value

    val date: LiveData<String>
        get() = _date

    val newReleases: LiveData<List<Item>>
        get() = _newReleases

    val trySomething: LiveData<List<Album>>
        get() = _trySomething

    val artists: LiveData<List<Artist>>
        get() = _artists


    fun setDateText() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("HH", Locale.getDefault())
        val hour = dateFormat.format(currentDate).toInt()

        val greeting = when (hour) {
            in 6..11 -> "Good morning!"
            in 12..15 -> "Good afternoon!"
            in 16..20 -> "Good evening!"
            else -> "Good night"
        }
        _date.postValue(greeting)
    }

    fun getNewRelease() {
        viewModelScope.launch {
            val response = albumApi.getNewReleases()
            if (response.isSuccessful) {
                _newReleases.postValue(response.body()?.albums?.items)
            }
        }
    }

    fun getTrySomething() {
        viewModelScope.launch {
            val query = "382ObEPsp2rxGrnsizN5TX%2C1A2GTWGtFfWp7KSQTwWOyo%2C2noRn2Aes5aoNVsU6iWThc"
            val response = albumApi.getSomeAlbums(query)
            if (response.isSuccessful) {
                _trySomething.postValue(response.body()?.albums)
            }
        }
    }


    fun getRoomArtistAlbum(context: Context) {
        val artistDao = RoomDB.accessDb(context)?.artistDao()
        viewModelScope.launch {
            val allArtistId = artistDao?.getArtistId()
            val artistIDs = allArtistId?.toSet()
            var artists = mutableListOf<Artist>()
            artistIDs?.forEach {
                val response = artistApi.getArtists(it)
                if(response.isSuccessful){
                    val artist = response.body()?.artists?.get(0)
                    artists.add(artist!!)
                }
            }
            _artists.postValue(artists)
        }
    }
}