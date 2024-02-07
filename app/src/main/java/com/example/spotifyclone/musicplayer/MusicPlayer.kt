package com.example.spotifyclone.musicplayer

import android.content.Context
import android.net.Uri
import android.media.MediaPlayer
import androidx.compose.runtime.remember
import com.example.spotifyclone.model.dto.MusicItem

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var listOfTracks: List<MusicItem>? = null

    fun initialize(context: Context, musicUriString: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, Uri.parse(musicUriString))
            mediaPlayer?.setVolume(0.5f, 0.5f)
        }


    }

    fun setListOfTracks(listOfTracks: List<MusicItem>) {
        this.listOfTracks = listOfTracks
    }

    fun getListOfTracks(): List<MusicItem>? {
        return listOfTracks
    }

    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    fun releaseMediaPlayer() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer = null
        }

    }

    fun prepare() {
        mediaPlayer?.prepare()
    }

    fun playNext(context: Context, uri: String) {
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(context, Uri.parse(uri))
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }


}