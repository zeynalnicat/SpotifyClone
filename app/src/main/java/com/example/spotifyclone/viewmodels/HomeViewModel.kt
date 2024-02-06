package com.example.spotifyclone.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.AlbumAdapter
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.album.newrelease.AlbumsX
import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.model.album.trysomething.Album
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.model.firebase.Albums
import com.example.spotifyclone.model.firebase.Tracks
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.retrofit.RetrofitInstance
import com.example.spotifyclone.retrofit.api.AlbumApi
import com.example.spotifyclone.retrofit.api.ArtistsApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val _date = MutableLiveData<String>()
    private val _newReleases = MutableLiveData<List<Item>>()
    private val _popularAlbums =
        MutableLiveData<List<com.example.spotifyclone.model.album.popularalbums.Album>>()
    private val _artists = MutableLiveData<List<Artist>>()
    private val albumApi = RetrofitInstance.getInstance()?.create(AlbumApi::class.java)!!
    private val artistApi = RetrofitInstance.getInstance()?.create(ArtistsApi::class.java)!!
    private val _recommended =
        MutableLiveData<Resource<List<com.example.spotifyclone.model.pseudo_models.Album>>>()

    val date: LiveData<String>
        get() = _date

    val newReleases: LiveData<List<Item>>
        get() = _newReleases

    val popularAlbums: LiveData<List<com.example.spotifyclone.model.album.popularalbums.Album>>
        get() = _popularAlbums

    val artists: LiveData<List<Artist>>
        get() = _artists

    val recommended: LiveData<Resource<List<com.example.spotifyclone.model.pseudo_models.Album>>>
        get() = _recommended


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
            val query =
                "7bPTIw59JU8w3NntSpmEzo,78bpIziExqiI9qztvNFlQu,5pSk3c3wVwnb2arb6ohCPU,5VoeRuTrGhTbKelUfwymwu,0ODLCdHBFVvKwJGeSfd1jy"
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
                if (response.isSuccessful) {
                    val artist = response.body()?.artists?.get(0)
                    artists.add(artist!!)
                }
            }
            _artists.postValue(artists)
        }
    }

    //Firebase
    fun setRecommended() {
        try {
            val database = FirebaseDatabase.getInstance()
            val refAlbums = database.getReference("albums")
            val listAlbums = mutableListOf<Albums>()
            refAlbums.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    for (v in ds.children) {
                        val albumMap = v.value as HashMap<*, *>
                        val tracksMap = albumMap["tracks"] as List<Map<*, *>>

                        val trackList = tracksMap.map { track ->
                            val trackMap = track
                            Tracks(
                                artist = trackMap["artist"] as String?,
                                id = trackMap["id"] as String?,
                                name = trackMap["name"] as String?,
                                trackUri = trackMap["trackUri"] as String?
                            )
                        }

                        val album = Albums(
                            albumMap["coverImg"] as String?,
                            albumMap["id"] as String?,
                            albumMap["name"] as String?,
                            trackList
                        )

                        listAlbums.add(album)
                    }

                    val albumModel = listAlbums.map {
                        com.example.spotifyclone.model.pseudo_models.Album(
                            it.coverImg ?: "",
                            it.id ?: "",
                            it.name ?: "",
                            it.tracks ?: emptyList(),
                            true
                        )
                    }

                    _recommended.postValue(Resource.Success(albumModel))


                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        } catch (e: Exception) {
            _recommended.postValue(Resource.Error(e))
        }

    }
}