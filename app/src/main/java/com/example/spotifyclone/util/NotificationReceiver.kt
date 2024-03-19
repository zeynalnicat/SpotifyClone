package com.example.spotifyclone.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.spotifyclone.service.MusicPlayerService
import com.example.spotifyclone.ui.activity.MainActivity

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val musicService = MainActivity.getMusicPlayerService()
        when (intent?.action) {
            MusicPlayerService.ACTION_NEXT -> {
                musicService?.nextSong()
            }
            MusicPlayerService.ACTION_PREVIOUS -> {

                musicService?.prevSong()
            }
            MusicPlayerService.ACTION_PAUSE -> {
                musicService?.pauseMusic()
            }
        }
    }




}
