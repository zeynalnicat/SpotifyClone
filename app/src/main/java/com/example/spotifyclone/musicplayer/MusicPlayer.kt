package com.example.spotifyclone.musicplayer

import android.content.Context
import android.net.Uri
import android.media.MediaPlayer

object MusicPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun initialize(context: Context, musicUriString: String) {
        mediaPlayer = MediaPlayer.create(context, Uri.parse(musicUriString))
        mediaPlayer?.setVolume(0.5f, 0.5f)

    }

    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}