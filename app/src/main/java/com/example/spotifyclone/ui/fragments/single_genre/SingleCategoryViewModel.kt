package com.example.spotifyclone.ui.fragments.single_genre

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyclone.model.deezer.DeezerTrack
import com.example.spotifyclone.network.retrofit.api.deezer.TrackApi
import com.example.spotifyclone.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleCategoryViewModel(private val trackApi: TrackApi):ViewModel() {

    private val _tracks = MutableLiveData<Resource<List<DeezerTrack>>>()

    val tracks: LiveData<Resource<List<DeezerTrack>>> get() = _tracks


    fun getTracks(ids:List<String>){
        _tracks.postValue(Resource.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val listTracks = mutableListOf<DeezerTrack>()
                for (id in ids) {
                    val trackId = id.toIntOrNull()

                    if (trackId != null) {
                        val response = trackApi.getTrack(trackId)

                        if (response.isSuccessful) {
                            response.body()?.let {
                                listTracks.add(it)
                            }
                        }
                    }
                }
                _tracks.postValue(Resource.Success(listTracks))

            }catch (e:Exception){
                    _tracks.postValue(Resource.Error(e))
            }
        }
    }

}