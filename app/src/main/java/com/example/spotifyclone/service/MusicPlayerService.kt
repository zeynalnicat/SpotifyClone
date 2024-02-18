package com.example.spotifyclone.service

import android.app.Service
import android.content.Intent

import android.media.MediaPlayer

import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.sp.SharedPreference


class MusicPlayerService : Service() {

    lateinit var mediaPlayer: MediaPlayer
    var songIndex: Int = 0
    var tracks: List<MusicItem> = emptyList()
    var currentUri = ""
    val musicIsplaying = MutableLiveData<Boolean>()
    private lateinit var sharedPreference: SharedPreference


    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        sharedPreference = SharedPreference(applicationContext)
        mediaPlayer.setOnCompletionListener {
            nextSong()
        }
    }


    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    fun playMusic(songUri: String) {
        if (musicIsplaying.value == true && tracks.isNotEmpty() && tracks.size > songIndex &&
            songUri == currentUri
        ) {
            return
        } else if (tracks.isNotEmpty()) {
            val track = tracks[songIndex]
            sharedPreference.saveSongsList(tracks)
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentUri = songUri
            mediaPlayer.setDataSource(currentUri)
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                musicIsplaying.postValue(true)
            }
            mediaPlayer.prepareAsync()
            if (!sharedPreference.containsValue(track.name)) {
                sharedPreference.saveValue("PlayingMusic", track.name)
                sharedPreference.saveValue("PlayingMusicArtist", track.artist)
                sharedPreference.saveValue("PlayingMusicUri", track.trackUri)
            }
        } else {
            tracks = sharedPreference.getSongsList()
        }
    }

    fun nextSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        songIndex = (songIndex + 1) % tracks.size
        val songUri: String = tracks[songIndex].trackUri
        playMusic(songUri)
    }

    fun prevSong() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        songIndex = (songIndex - 1) % tracks.size
        val songUri = tracks[songIndex].trackUri
        playMusic(songUri)
    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            musicIsplaying.postValue(false)
        }
    }

    fun currentTrack(): MusicItem {
        return tracks[songIndex]
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tracks = intent?.getSerializableExtra("tracks") as? List<MusicItem> ?: emptyList()
        return START_NOT_STICKY
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }


    override fun onBind(intent: Intent): IBinder {

        return MusicPlayerBinder()
    }

    override fun onDestroy() {
        mediaPlayer.release()
    }

    override fun onTaskRemoved(rootIntent: Intent) {

        super.onTaskRemoved(rootIntent)
    }

}