package com.example.spotifyclone.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicPlayerViewModel : ViewModel() {
    private val _selectedTrackPosition = MutableLiveData<Int>()
    val selectedTrackPosition: LiveData<Int>
        get() = _selectedTrackPosition


    private val _currentTrack = MutableLiveData<com.example.spotifyclone.domain.model.dto.MusicItem>()

    private val _tracks = MutableLiveData<List<com.example.spotifyclone.domain.model.dto.MusicItem>>()

    private val _current = MutableLiveData<com.example.spotifyclone.domain.model.dto.MusicItem>()

    val current :LiveData<com.example.spotifyclone.domain.model.dto.MusicItem> get() = _current
    val tracks: LiveData<List<com.example.spotifyclone.domain.model.dto.MusicItem>>
        get() = _tracks

    val currentTrack: LiveData<com.example.spotifyclone.domain.model.dto.MusicItem> get() = _currentTrack

    fun setSelectedTrackPosition(position: Int) {
        _selectedTrackPosition.value = position
    }

    fun setTracks(tracks: List<com.example.spotifyclone.domain.model.dto.MusicItem>) {
        _tracks.value = tracks
    }

    fun setCurrentMusic(musicItem: com.example.spotifyclone.domain.model.dto.MusicItem) {
        _currentTrack.postValue(musicItem)
    }

    fun setPosition(pos:Int){
        _selectedTrackPosition.postValue(pos)
    }

    fun getCurrent(musicItem: com.example.spotifyclone.domain.model.dto.MusicItem){
        _current.postValue(musicItem)
    }

}
