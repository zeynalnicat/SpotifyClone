package com.example.spotifyclone.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.util.GsonHelper

class MusicRepository(private val applicationContext: Context) {
    val tracksLiveData = MutableLiveData<List<MusicItem>>()

    init {
        loadTracks()
    }

    fun loadTracks() {
        val newTracks = GsonHelper.deserializeTracks(applicationContext)
        tracksLiveData.postValue(newTracks)
    }
}
