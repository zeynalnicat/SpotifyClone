package com.example.spotifyclone.domain

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.spotifyclone.domain.model.dto.MusicItem
import com.example.spotifyclone.util.GsonHelper

class MusicRepository(private val applicationContext: Context) {
    val tracksLiveData = MutableLiveData<List<MusicItem>>()


    fun loadTracks() {
        val newTracks = GsonHelper.deserializeTracks(applicationContext)
        tracksLiveData.value = newTracks
    }


}
