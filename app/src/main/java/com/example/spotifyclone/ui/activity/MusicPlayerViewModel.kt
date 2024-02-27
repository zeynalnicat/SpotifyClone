package com.example.spotifyclone.ui.activity

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spotifyclone.model.dto.MusicItem

class MusicPlayerViewModel : ViewModel() {
    private val _selectedTrackPosition = MutableLiveData<Int>()
    val selectedTrackPosition: LiveData<Int>
        get() = _selectedTrackPosition


    private val _currentTrack = MutableLiveData<MusicItem>()

    private val _tracks = MutableLiveData<List<MusicItem>>()
    val tracks: LiveData<List<MusicItem>>
        get() = _tracks

    val currentTrack: LiveData<MusicItem> get() = _currentTrack

    fun setSelectedTrackPosition(position: Int) {
        _selectedTrackPosition.value = position
    }

    fun setTracks(tracks: List<MusicItem>) {
        _tracks.value = tracks
    }

    fun setCurrentMusic(musicItem: MusicItem) {
        _currentTrack.postValue(musicItem)
    }

}
