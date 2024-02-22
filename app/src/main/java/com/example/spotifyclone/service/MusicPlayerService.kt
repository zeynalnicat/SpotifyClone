package com.example.spotifyclone.service

import android.app.Application
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.util.GsonHelper

class MusicPlayerService : Service() {

    lateinit var mediaPlayer: MediaPlayer
    var songIndex = 0
    var tracks = MutableLiveData<List<MusicItem>>()
    private var currentUri = ""
    val musicIsPlaying = MutableLiveData<Boolean>()
    private lateinit var sharedPreference: SharedPreference

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()

        sharedPreference = SharedPreference(applicationContext)

        tracks.postValue(GsonHelper.deserializeTracks(applicationContext))
        tracks.observeForever {
            if (!it.isEmpty() && it != null) {
                songIndex = sharedPreference.getValue("Position", 0)
                playMusic(it[songIndex].trackUri)
            }

        }


    }


    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    fun setTracks(tracks: List<MusicItem>, position: Int) {
        this.tracks.postValue(tracks)
        songIndex = position
    }

    fun playAll() {
        songIndex = 0
        playMusic(tracks.value?.get(songIndex)?.trackUri ?: "")
    }

    fun playMusic(songUri: String) {
        if (musicIsPlaying.value == true && tracks.value?.isNotEmpty() == true &&
            tracks.value?.size?.compareTo(songIndex) == 1 && songUri == currentUri
        ) {
            return
        } else if (tracks.value?.isNotEmpty() == true) {
            val track = tracks.value?.get(songIndex)
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentUri = songUri
            mediaPlayer.setDataSource(currentUri)

            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                musicIsPlaying.postValue(true)
                sharedPreference.saveValue("PlayingMusic", track?.name ?: "")
                sharedPreference.saveValue("PlayingMusicArtist", track?.artist ?: "")
                sharedPreference.saveValue("PlayingMusicUri", track?.trackUri ?: "")

            }


            mediaPlayer.prepareAsync()


            if (!GsonHelper.hasTracks(applicationContext)) {
                GsonHelper.serializeTracks(applicationContext, tracks.value ?: emptyList())
            }
        } else {
            tracks.value = sharedPreference.getSongsList()
        }
    }

    fun nextSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        val index = songIndex
        val newIndex = (index + 1) % (tracks.value?.size ?: 0)
        Log.e("Tracks", tracks.value.toString())
        songIndex = newIndex
        sharedPreference.saveValue("Position", songIndex)
        val songUri: String = tracks.value?.get(songIndex)?.trackUri ?: ""
        playMusic(songUri)
    }

    fun prevSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        val index = songIndex
        val newIndex = (index - 1) % (tracks.value?.size ?: 0)
        songIndex = newIndex
        sharedPreference.saveValue("Position", songIndex)
        val songUri = tracks.value?.get(songIndex)?.trackUri ?: ""
        playMusic(songUri)
    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            musicIsPlaying.postValue(false)
        }
    }

    fun currentTrack(): MusicItem {
        return tracks.value?.get(songIndex) ?: MusicItem("", "", "", "")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val receivedTracks = intent?.getSerializableExtra("tracks") as? ArrayList<MusicItem>
        tracks.postValue(receivedTracks?.toList())
        return START_NOT_STICKY
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    override fun onBind(intent: Intent): IBinder {
        return MusicPlayerBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
    }

}
