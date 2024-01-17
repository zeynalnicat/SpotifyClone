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
import com.example.spotifyclone.retrofit.api.AlbumApi
import com.example.spotifyclone.retrofit.api.ArtistsApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val _date = MutableLiveData<String>()
    private val _newReleases = MutableLiveData<List<Item>>()
    private val _popularAlbums = MutableLiveData<List<com.example.spotifyclone.model.album.popularalbums.Album>>()
    private val _artists = MutableLiveData<List<Artist>>()
    private val albumApi =  RetrofitInstance.getInstance()?.create(AlbumApi::class.java)!!
    private val artistApi =  RetrofitInstance.getInstance()?.create(ArtistsApi::class.java)!!

    val date: LiveData<String>
        get() = _date

    val newReleases: LiveData<List<Item>>
        get() = _newReleases

    val popularAlbums: LiveData<List<com.example.spotifyclone.model.album.popularalbums.Album>>
        get() = _popularAlbums

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

    fun getPopularAlbums() {
        viewModelScope.launch {
            val query = "7bPTIw59JU8w3NntSpmEzo,78bpIziExqiI9qztvNFlQu,5pSk3c3wVwnb2arb6ohCPU,5VoeRuTrGhTbKelUfwymwu,0ODLCdHBFVvKwJGeSfd1jy"
            val response = albumApi.getSomeAlbums(query)
            if (response.isSuccessful) {
                _popularAlbums.postValue(response.body()?.albums)
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