package com.example.spotifyclone.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.util.GsonHelper

class MusicPlayerService : Service() {
    private lateinit var repository: MusicRepository
    lateinit var mediaPlayer: MediaPlayer
    var songIndex: Int = 0
    var tracks = MutableLiveData<List<MusicItem>>()
    var currentUri = ""
    val musicIsPlaying = MutableLiveData<Boolean>()
    private lateinit var sharedPreference: SharedPreference

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        repository = MusicRepository(applicationContext)
        sharedPreference = SharedPreference(applicationContext)
        songIndex = sharedPreference.getValue("Position", 0)

        tracks.observeForever{
            musicIsPlaying.postValue(true)
            sharedPreference.saveValue("PlayingMusic", it[songIndex].name )
            sharedPreference.saveValue("PlayingMusicArtist", it[songIndex].artist )
            sharedPreference.saveValue("PlayingMusicUri", it[songIndex].trackUri )
        }
        repository.tracksLiveData.observeForever { newTracks ->
            handleTracksUpdate(newTracks)
        }
    }


    private fun handleTracksUpdate(newTracks: List<MusicItem>) {

        if (newTracks.isNotEmpty()) {
            songIndex = 0
            playMusic(newTracks[songIndex].trackUri)
        }
    }

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    fun playAll() {

        songIndex = 0
        playMusic(tracks.value?.get(songIndex)?.trackUri?:"")

    }

    fun playMusic(songUri: String) {
        if (musicIsPlaying.value == true && tracks.value?.isNotEmpty() == true &&
            tracks.value?.size?.compareTo(songIndex)==1 && songUri == currentUri
        ) {
            return
        } else if (tracks.value?.isNotEmpty() == true) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentUri = songUri
            mediaPlayer.setDataSource(currentUri)
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()

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
        songIndex = (songIndex + 1) % (tracks.value?.size ?: 0)
        val songUri: String = tracks.value?.get(songIndex)?.trackUri ?: ""
        playMusic(songUri)
    }

    fun prevSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        songIndex = (songIndex - 1 + (tracks.value?.size ?: 0)) % (tracks.value?.size ?: 0)
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
        // Handle the intent if needed
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
