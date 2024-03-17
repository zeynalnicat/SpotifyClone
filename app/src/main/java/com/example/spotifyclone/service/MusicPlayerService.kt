package com.example.spotifyclone.service

import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.spotifyclone.R
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.util.GsonHelper


class MusicPlayerService : Service() {

    lateinit var mediaPlayer: MediaPlayer
    var songIndex = MutableLiveData(0)
    var tracks = MutableLiveData<List<MusicItem>>()
    private var currentUri = ""
    val musicIsPlaying = MutableLiveData<Boolean>()
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var sharedPreference: SharedPreference

    companion object {
        const val ACTION_SET_TRACKS = "com.example.spotifyclone.SET_TRACKS"
        const val EXTRA_TRACKS_JSON = "com.example.spotifyclone.EXTRA_TRACKS_JSON"
        const val EXTRA_ACTION_TYPE = "com.example.spotifyclone.EXTRA_ACTION_TYPE"
        const val BROADCAST_ACTION = "com.example.spotifyclone.BROADCAST_ACTION"
        const val ACTION_NEXT = "com.example.spotifyclone.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.spotifyclone.ACTION_PREVIOUS"
    }

    enum class Actions() {
        START, STOP
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()

        sharedPreference = SharedPreference(applicationContext)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(BROADCAST_ACTION))

        tracks.observeForever {
            if (!it.isEmpty() && it != null) {
                val index = songIndex.value!!
                sharedPreference.saveValue("Position", index)
                saveSharedPreference(
                    it[index].img,
                    it[index].name,
                    it[index].artist,
                    it[index].trackUri
                )
                playMusic(it[index].trackUri)
            }

        }

        songIndex.observeForever {
            if (it != null) {
                if (tracks.value != null && tracks.value?.isNotEmpty() == true && it < tracks.value?.size!!) {
                    val track = tracks.value?.get(it)!!
                    sharedPreference.saveValue("Position", it)
                    saveSharedPreference(track.img, track.name, track.artist, track.trackUri)
                    playMusic(track.trackUri)

                }
            }
        }


    }


    fun saveSharedPreference(img: String, name: String, artist: String, uri: String) {
        sharedPreference.saveValue("PlayingMusicImg", img)
        sharedPreference.saveValue("PlayingMusic", name)
        sharedPreference.saveValue("PlayingMusicArtist", artist)
        sharedPreference.saveValue("PlayingMusicUri", uri)
    }


    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }


    fun setNotification() {
        val track = tracks.value?.get(songIndex.value ?: 0) ?: return

        val notification = NotificationCompat.Builder(this, "running_channel")
            .setContentTitle(track.name)
            .setContentText(track.artist)
            .setSmallIcon(R.drawable.logo)
            .setStyle(MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.icon_music_view_previous,
                    "Previous",
                    null
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.icon_music_play,
                    if (mediaPlayer.isPlaying) "Pause" else "Play",  // Change text based on playback state
                    null
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.icon_music_view_next,
                    "Next",
                    null
                ).build()
            )
            .build()

//        startForeground(1, notification)
    }



    fun setTracks(tracks: List<MusicItem>, position: Int) {
        this.tracks.postValue(tracks)
        songIndex.postValue(position)
    }

    fun setPosition(pos: Int) {
        songIndex.postValue(pos)
    }

    fun playAll() {
        if (tracks.value?.isNotEmpty() == true) {
            songIndex.postValue(0)
            sharedPreference.saveIsPlaying(true)
        }

    }

    fun playMusic(songUri: String) {
        val index = songIndex.value!!
        if (musicIsPlaying.value == true && tracks.value?.isNotEmpty() == true &&
            tracks.value?.size?.compareTo(index) == 1 && songUri == currentUri
        ) {
            return
        } else if (tracks.value?.isNotEmpty() == true) {
            setNotification()
            sharedPreference.saveValue("Position", index)
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentUri = songUri
            mediaPlayer.setDataSource(currentUri)
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
                musicIsPlaying.postValue(true)

            }


            mediaPlayer.prepareAsync()
            if (!GsonHelper.hasTracks(applicationContext)) {
                GsonHelper.serializeTracks(applicationContext, tracks.value ?: emptyList())
            }
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val actionType = it.getStringExtra(EXTRA_ACTION_TYPE)
                handleAction(actionType)
            }
        }
    }

    private fun handleAction(actionType: String?) {
        when (actionType) {
            ACTION_NEXT -> {
                nextSong()
            }

            ACTION_PREVIOUS -> {
                prevSong()
            }

        }
    }

    fun nextSong() {
        mediaPlayer.reset()
        val index = songIndex.value!!
        val newIndex = (index + 1) % (tracks.value?.size ?: 0)
        Log.e("Tracks", tracks.value.toString())
        songIndex.value = newIndex
        sharedPreference.saveValue("Position", newIndex)
        saveSharedPreference(
            tracks.value?.get(newIndex)?.img ?: "",
            tracks.value?.get(newIndex)?.name ?: "",
            tracks.value?.get(newIndex)?.artist ?: "",
            tracks.value?.get(newIndex)?.trackUri ?: ""
        )
        val songUri: String = tracks.value?.get(newIndex)?.trackUri ?: ""
        playMusic(songUri)
    }

    fun prevSong() {

        mediaPlayer.reset()
        val index = songIndex.value!!
        val newIndex = (index - 1) % (tracks.value?.size ?: 0)
        val currentIndex = if (newIndex < 0) tracks.value?.size!! - 1 else newIndex
        songIndex.value = currentIndex
        sharedPreference.saveValue("Position", songIndex.value!!)
        saveSharedPreference(
            tracks.value?.get(currentIndex)?.img ?: "",
            tracks.value?.get(currentIndex)?.name ?: "",
            tracks.value?.get(currentIndex)?.artist ?: "",
            tracks.value?.get(currentIndex)?.trackUri ?: ""
        )
        val songUri = tracks.value?.get(currentIndex)?.trackUri ?: ""
        playMusic(songUri)
    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()

        }
    }

    fun startMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }


    fun stopMusic() {
        mediaPlayer.stop()

    }

    fun currentTrack(): MusicItem {
        val index = songIndex.value!!
        return tracks.value?.get(index) ?: MusicItem("", "", "", "")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            ACTION_SET_TRACKS -> {
                val tracks = intent.getSerializableExtra(EXTRA_TRACKS_JSON) as? ArrayList<MusicItem>
                this.tracks.postValue(tracks)

            }


        }
        return START_STICKY
    }


    override fun onBind(intent: Intent): IBinder {
        mediaSession = MediaSessionCompat(baseContext,"Spotify")
//        mediaPlayer.setOnCompletionListener {
//            nextSong()
//        }
        return MusicPlayerBinder()
    }


    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
    }

//    override fun onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
//        super.onDestroy()
//    }
}
