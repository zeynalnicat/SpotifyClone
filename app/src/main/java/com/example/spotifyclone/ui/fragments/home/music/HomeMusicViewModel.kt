package com.example.spotifyclone.ui.fragments.home.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.deezer.DeezerTrack
import com.example.spotifyclone.network.retrofit.api.deezer.TrackApi
import com.example.spotifyclone.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeMusicViewModel(private val trackApi: TrackApi) : ViewModel() {

    private val _tracks = MutableLiveData<Resource<List<DeezerTrack>>>()

    val tracks: LiveData<Resource<List<DeezerTrack>>> get() = _tracks


    fun getTracks() {

        _tracks.postValue(Resource.Loading)
        val ids = listOf(
            3135556,
            3135544,
            3135534,
            3135524,
            3135539,
            3135512,
            3135598,
            3135584,
            3135571,
            3135559,
            3135542
        )

        viewModelScope.launch(Dispatchers.IO) {

            try {
                val list = mutableListOf<DeezerTrack>()
                withContext(Dispatchers.Default){
                    for (id in ids) {
                        val response = trackApi.getTrack(id)
                        if (response.isSuccessful) {
                            val track = response.body()
                            track?.let {
                                list.add(it)
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main){
                    _tracks.postValue(Resource.Success(list))
                }

            } catch (e: Exception) {
                _tracks.postValue(Resource.Error(e))
            }

        }
    }

}