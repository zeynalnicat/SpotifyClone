package com.example.spotifyclone.ui.fragments.home.music.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.data.network.api.deezer.TrackApi
import com.example.spotifyclone.domain.model.deezer.DeezerTrack
import com.example.spotifyclone.domain.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeMusicViewModel(private val trackApi: TrackApi) : ViewModel() {

    private val _tracks = MutableLiveData<Resource<List<DeezerTrack>>>()

    val tracks: LiveData<Resource<List<DeezerTrack>>> get() = _tracks


    fun getTracks() {

        _tracks.postValue(Resource.Loading)
        val ids = listOf(
            655096022,
            66402090,
            1157470,
            3135524,
            2317363,
            1474534502,
            117300904,
            67064117,
            528330501,
            416180702,
            1733665717,
            831601252,
            595112162,
            64482460

        )

        viewModelScope.launch(Dispatchers.IO) {

            try {
                val list = mutableListOf<com.example.spotifyclone.domain.model.deezer.DeezerTrack>()
                withContext(Dispatchers.Default){
                    for (id in ids) {
                        val response = trackApi.getTrack(id)
                        if (response.isSuccessful) {
                            val track = response.body()
                            track?.let {
                                list.add(it)
                            }
                            _tracks.postValue(Resource.Success(list))
                        }
                    }
                }

            } catch (e: Exception) {
                _tracks.postValue(Resource.Error(e))
            }

        }
    }

}