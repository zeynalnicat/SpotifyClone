package com.example.spotifyclone.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.model.firebase.Albums
import com.example.spotifyclone.model.firebase.Tracks
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.network.retrofit.api.ArtistsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val roomDB: RoomDB,
    private val albumApi: AlbumApi,
    private val artistApi: ArtistsApi,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _date = MutableLiveData<String>()
    private val _newReleases = MutableLiveData<Resource<List<Item>>>()
    private val _popularAlbums =
        MutableLiveData<Resource<List<com.example.spotifyclone.model.album.popularalbums.Album>>>()
    private val _artists = MutableLiveData<Resource<List<Artist>>>()
    private val _recommended =
        MutableLiveData<Resource<List<com.example.spotifyclone.model.dto.Album>>>()

    val date: LiveData<String>
        get() = _date

    val newReleases: LiveData<Resource<List<Item>>>
        get() = _newReleases

    val popularAlbums: LiveData<Resource<List<com.example.spotifyclone.model.album.popularalbums.Album>>>
        get() = _popularAlbums

    val artists: LiveData<Resource<List<Artist>>>
        get() = _artists

    val recommended: LiveData<Resource<List<com.example.spotifyclone.model.dto.Album>>>
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
            try {
                val response = albumApi.getNewReleases()
                if (response.isSuccessful) {
                    val result = response.body()?.albums?.items
                    result?.let {
                        _newReleases.postValue(Resource.Success(it))
                    }

                } else {
                    _newReleases.postValue(Resource.Error(Exception("There was an error")))
                }
            } catch (e: Exception) {
                _newReleases.postValue(Resource.Error(e))
            }

        }
    }

    fun getPopularAlbums() {
        viewModelScope.launch {
            try {
                val query =
                    "7bPTIw59JU8w3NntSpmEzo,78bpIziExqiI9qztvNFlQu,5pSk3c3wVwnb2arb6ohCPU,5VoeRuTrGhTbKelUfwymwu,0ODLCdHBFVvKwJGeSfd1jy"
                val response = albumApi.getSomeAlbums(query)
                if (response.isSuccessful) {
                    val result = response.body()?.albums
                    result?.let {
                        _popularAlbums.postValue(Resource.Success(it))
                    }

                } else {
                    _popularAlbums.postValue(Resource.Error(Exception("There was an error")))
                }

            } catch (e: Exception) {
                _popularAlbums.postValue(Resource.Error(e))
            }

        }
    }


    fun getArtist() {
        val artistRef = firestore.collection("favArtist")
        val userId = firebaseAuth.currentUser?.uid
        val query = artistRef.whereEqualTo("userId", userId)
        try {
            val artistIDs = hashSetOf<String>()
            val artists = mutableListOf<Artist>()
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documents = querySnapshot.documents
                        for (document in documents) {
                            artistIDs.add(document["artistId"] as String)
                        }
                        viewModelScope.launch(Dispatchers.IO) {
                            artistIDs.forEach {
                                val response = artistApi.getArtists(it)
                                if (response.isSuccessful) {
                                    val artist = response.body()?.artists?.get(0)
                                    artist?.let { artist ->
                                        artists.add(artist)
                                    }
                                }
                            }
                            if (artists.size > 0) {
                                _artists.postValue(Resource.Success(artists))
                            } else {
                                _artists.postValue(Resource.Error(Exception("There was an error ")))
                            }
                        }

                    }
                }


        } catch (e: Exception) {
            _artists.postValue(Resource.Error(e))
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
                        com.example.spotifyclone.model.dto.Album(
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