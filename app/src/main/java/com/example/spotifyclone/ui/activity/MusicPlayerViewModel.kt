package com.example.spotifyclone.ui.activity

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicPlayerViewModel:ViewModel() {
    val mediaPlayer: MediaPlayer? = MediaPlayer()
    val isPlaying = MutableLiveData<Boolean>()
    val currentDuration = MutableLiveData<Int>()
    val totalDuration = MutableLiveData<Int>()
}