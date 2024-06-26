package com.example.spotifyclone.ui.fragments.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.data.network.TokenRefresher
import com.example.spotifyclone.data.network.api.AlbumApi
import com.example.spotifyclone.data.network.api.ArtistsApi
import com.example.spotifyclone.data.network.api.deezer.TrackApi
import com.example.spotifyclone.domain.model.artist.Artist
import com.example.spotifyclone.domain.model.dto.Album
import com.example.spotifyclone.domain.model.dto.MusicItem
import com.example.spotifyclone.domain.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val albumApi: AlbumApi,
    private val artistApi: ArtistsApi,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val tokenRefresher: TokenRefresher,
    private val trackApi: TrackApi,
    private val albumDeezerApi: com.example.spotifyclone.data.network.api.deezer.AlbumApi
) : ViewModel() {

    private val _date = MutableLiveData<String>()
    private val _newReleases = MutableLiveData<Resource<List<Album>>>()
    private val _popularAlbums =
        MutableLiveData<Resource<List<Album>>>()
    private val _artists = MutableLiveData<Resource<List<Artist>>>()
    private val _recommended =
        MutableLiveData<Resource<List<Album>>>()

    private val _topMusics = MutableLiveData<Resource<List<MusicItem>>>()

    private val _someAlbums =
        MutableLiveData<Resource<List<Album>>>()

    val topMusics: LiveData<Resource<List<MusicItem>>> get() = _topMusics

    val someAlbums: LiveData<Resource<List<Album>>> get() = _someAlbums
    val date: LiveData<String>
        get() = _date

    val newReleases: LiveData<Resource<List<Album>>>
        get() = _newReleases

    val popularAlbums: LiveData<Resource<List<Album>>>
        get() = _popularAlbums

    val artists: LiveData<Resource<List<Artist>>>
        get() = _artists

    val recommended: LiveData<Resource<List<Album>>>
        get() = _recommended


    private suspend fun <T> refreshTokenAndExecute(apiCall: suspend () -> Response<T>): Response<T> {
        if (tokenRefresher.isAccessTokenExpired()) {
            Log.d("TokenRefresh", "Refreshing access token")
            tokenRefresher.refreshAccessToken()
        }
        Log.d("TokenRefresh", "Executing API call")
        val response = apiCall.invoke()
        Log.d("TokenRefresh", "API call response: $response")
        return response
    }

    fun setDateText() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("HH", Locale.getDefault())
        val hour = dateFormat.format(currentDate).toInt()

        val greeting = when (hour) {
            in 6..11 -> "Good morning!"
            in 12..17 -> "Good afternoon!"
            in 18..23 -> "Good evening!"
            else -> "Good night!"
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
                        val model = it.map {
                            com.example.spotifyclone.domain.model.dto.Album(
                                it.images[0].url,
                                it.id,
                                it.name,
                                emptyList()
                            )
                        }
                        _newReleases.postValue(Resource.Success(model))
                    }

                } else {
                    _newReleases.postValue(Resource.Error(Exception("There was an error")))
                    getDeezerAlbums()
                }
            } catch (e: Exception) {
                _newReleases.postValue(Resource.Error(e))
                getDeezerAlbums()
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
                        val model = it.map {
                            com.example.spotifyclone.domain.model.dto.Album(
                                it.images[0].url,
                                it.id,
                                it.name,
                                emptyList()
                            )
                        }
                        _popularAlbums.postValue(Resource.Success(model))
                    }

                } else {
                    _popularAlbums.postValue(Resource.Error(Exception("There was an error")))
                    getSomeAlbums()
                }

            } catch (e: Exception) {
                _popularAlbums.postValue(Resource.Error(e))
                getSomeAlbums()
            }

        }
    }


    fun getArtist() {
        val artistRef = firestore.collection("favArtist")
        val userId = firebaseAuth.currentUser?.uid
        val query = artistRef.whereEqualTo("userId", userId)

        try {
            val artistIDs = hashSetOf<String>()
            val artists = mutableListOf<com.example.spotifyclone.domain.model.artist.Artist>()
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documents = querySnapshot.documents
                        for (document in documents) {
                            artistIDs.add(document["artistId"] as String)
                        }

                        viewModelScope.launch(Dispatchers.IO) {
                            artistIDs.forEach {
                                val response = refreshTokenAndExecute { artistApi.getArtists(it) }
                                if (response.isSuccessful) {
                                    val artist = response.body()?.artists?.get(0)
                                    artist?.let { artist ->
                                        artists.add(artist)
                                    }
                                }
                            }

                            withContext(Dispatchers.Main) {
                                if (artists.size > 0) {
                                    _artists.postValue(Resource.Success(artists))
                                } else {
                                    _artists.postValue(Resource.Error(Exception("There was an error ")))
                                }
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
            val listAlbums = mutableListOf<com.example.spotifyclone.domain.model.firebase.Albums>()
            refAlbums.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    for (v in ds.children) {
                        val albumMap = v.value as HashMap<*, *>
                        val tracksMap = albumMap["tracks"] as List<Map<*, *>>

                        val trackList = tracksMap.map { track ->
                            val trackMap = track
                            com.example.spotifyclone.domain.model.firebase.Tracks(
                                artist = trackMap["artist"] as String?,
                                id = trackMap["id"] as String?,
                                name = trackMap["name"] as String?,
                                trackUri = trackMap["trackUri"] as String?
                            )
                        }

                        val album = com.example.spotifyclone.domain.model.firebase.Albums(
                            albumMap["coverImg"] as String?,
                            albumMap["id"] as String?,
                            albumMap["name"] as String?,
                            trackList
                        )

                        listAlbums.add(album)
                    }

                    val albumModel = listAlbums.map {
                        com.example.spotifyclone.domain.model.dto.Album(
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
                    _recommended.postValue(Resource.Error(Exception(error.message)))
                }
            })
        } catch (e: Exception) {
            _recommended.postValue(Resource.Error(e))
        }

    }


    fun getTopMusics() {
        val idsMusic = listOf(89077549, 509382892, 82715364, 6461432, 1151534112, 74427068)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listMusic = mutableListOf<com.example.spotifyclone.domain.model.dto.MusicItem>()

                idsMusic.forEach {
                    val response = trackApi.getTrack(it)
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let { track ->
                            val musicItem = com.example.spotifyclone.domain.model.dto.MusicItem(
                                track.artist.name,
                                "" + track.id,
                                track.title,
                                track.preview,
                                track.album.cover_xl
                            )
                            listMusic.add(musicItem)
                        }
                    }

                    _topMusics.postValue(Resource.Success(listMusic))
                }
            } catch (e: Exception) {
                _topMusics.postValue(Resource.Error(e))
            }
        }

    }

    fun getSomeAlbums() {
        val albumIds = listOf(104188,988431,91598612, 7350949, 365439377, 219247562)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listMusic = mutableListOf<com.example.spotifyclone.domain.model.dto.Album>()

                albumIds.forEach {
                    val response = albumDeezerApi.getAlbum(it)
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            val albumItem = com.example.spotifyclone.domain.model.dto.Album(
                                it.cover_xl,
                                "" + it.id,
                                it.title,
                                it.tracks.data.map { track ->
                                    com.example.spotifyclone.domain.model.firebase.Tracks(
                                        track.artist.name,
                                        "",
                                        track.title,
                                        track.preview,
                                    )
                                }, isFirebase = true, isDeezer = true
                            )
                            listMusic.add(albumItem)
                        }
                        _popularAlbums.postValue(Resource.Success(listMusic))

                    }
                }

            } catch (e: Exception) {
                _popularAlbums.postValue(Resource.Error(e))
            }
        }
    }


    fun getDeezerAlbums(){
        val albumIds = listOf(6470389,8864149,125290822,6000937,215835692)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listMusic = mutableListOf<com.example.spotifyclone.domain.model.dto.Album>()

                albumIds.forEach {
                    val response = albumDeezerApi.getAlbum(it)
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            val albumItem = com.example.spotifyclone.domain.model.dto.Album(
                                it.cover_xl,
                                "" + it.id,
                                it.title,
                                it.tracks.data.map { track ->
                                    com.example.spotifyclone.domain.model.firebase.Tracks(
                                        track.artist.name,
                                        "",
                                        track.title,
                                        track.preview,
                                    )
                                }, isFirebase = true, isDeezer = true
                            )
                            listMusic.add(albumItem)
                        }
                        _newReleases.postValue(Resource.Success(listMusic))

                    }
                }

            } catch (e: Exception) {
                _newReleases.postValue(Resource.Error(e))
            }
        }
    }

}