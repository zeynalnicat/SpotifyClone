package com.example.spotifyclone.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ActivityMainBinding
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.service.MusicPlayerService
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.fragments.track.TrackViewFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: android.os.Handler
    private var musicPlayerService: MusicPlayerService? = null
    private var totalTime: Int = 0
    private var position = 0
    private var tracks: List<MusicItem> = emptyList()
    private lateinit var sharedPreference: SharedPreference

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MusicPlayerService::class.java)
        tracks = sharedPreference.getSongsList()
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreference = SharedPreference(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        setNavigation()


    }

    fun getMediaPlayer(): MediaPlayer? {
        return musicPlayerService?.mediaPlayer
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicPlayerService = binder.getService()
            checkVisibility()
            musicPlayerService?.let {
                it.mediaPlayer.let { mediaPlayer ->
                    totalTime = mediaPlayer.duration
                    handler = android.os.Handler(Looper.getMainLooper())
                    updateProgress()

                    binding.imgPause.setOnClickListener { view ->
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                            binding.imgPause.setImageResource(R.drawable.icon_music_play)
                        } else {
                            mediaPlayer.start()
                            binding.imgPause.setImageResource(R.drawable.icon_music_pause)
                        }
                    }
                }

            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    fun checkVisibility() {
        val sharedPreference = SharedPreference(this)
        if (sharedPreference.getValue("isPlaying", false)) {
            setMusicPlayer(true)
        }
    }

    fun setBottomNavigation(visibility: Boolean) {
        if (visibility) {
            binding.bottomNav.visibility = View.VISIBLE
            binding.bottomNav.itemIconTintList = null;

        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }

    fun setMusicPlayer(visibility: Boolean) {
        if (visibility) {
            binding.musicPlayer.visibility = View.VISIBLE
            setMusicAttrs()
        } else {
            binding.musicPlayer.visibility = View.GONE
        }
    }

    fun setTracksAndPosition(tracks: List<MusicItem>, position: Int) {
        this.position = position
        this.tracks = tracks
        musicPlayerService?.tracks = tracks
        musicPlayerService?.songIndex = position
    }

    fun setMusicAttrs() {
        val sharedPreference = SharedPreference(this)
        val musicName = sharedPreference.getValue("PlayingMusic", "")
        val musicImg = sharedPreference.getValue("PlayingMusicImg", "")
        val musicUri = sharedPreference.getValue("PlayingMusicUri", "")

        setMusicLayout(musicName, musicImg, musicUri)

    }

    private fun setMusicLayout(name: String, img: String, uri: String) {
        musicPlayerService?.playMusic(uri)

        Glide.with(binding.root)
            .load(img)
            .into(binding.imgTrack)


        binding.txtMusicName.text = name
    }


    private fun updateProgress() {
        musicPlayerService?.mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                val currentDuration = mediaPlayer.currentPosition
                val progress = (currentDuration.toFloat() / totalTime * 100).toInt()
                binding.progressBar.progress = progress
                handler.postDelayed({ updateProgress() }, 100)
            }
        }
    }


    private fun setNavigation() {
        binding.musicPlayer.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            val trackViewFragment = TrackViewFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, trackViewFragment)
                .addToBackStack(null)
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .commit()
            setMusicPlayer(false)
        }
    }

    fun getCurrentTrack(): MusicItem {

        return musicPlayerService?.currentTrack()!!
    }


    fun pauseMusic() {
        musicPlayerService?.mediaPlayer?.pause()
    }

    fun nextSong() {
        musicPlayerService?.nextSong()
    }

    fun prevSong() {
        musicPlayerService?.prevSong()
    }

    fun playAll() {
        musicPlayerService?.playAll()
    }


}