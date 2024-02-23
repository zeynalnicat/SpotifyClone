package com.example.spotifyclone.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ActivityMainBinding
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.service.MusicPlayerService
import com.example.spotifyclone.service.MusicRepository
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.fragments.track.TrackViewFragment
import com.example.spotifyclone.util.GsonHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: android.os.Handler
    private var musicPlayerService: MusicPlayerService? = null
    private lateinit var musicPlayerViewModel: MusicPlayerViewModel

    private lateinit var repository: MusicRepository
    private var tracks = MutableLiveData<List<MusicItem>>(emptyList())
    private lateinit var sharedPreference: SharedPreference
    private var position = 0

    override fun onStart() {
        super.onStart()

        startService()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        musicPlayerViewModel = ViewModelProvider(this)[MusicPlayerViewModel::class.java]
        sharedPreference = SharedPreference(this)
        repository = MusicRepository(applicationContext)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        setNavigation()

        musicPlayerViewModel.selectedTrackPosition.observe(this) { position ->
            this.position = position
            setMusicPlayer(true)
        }
        musicPlayerViewModel.tracks.observe(this) { tracks ->
            setTracksAndPosition(tracks, position)
        }


        repository = MusicRepository(applicationContext)
        repository.loadTracks()

        repository.tracksLiveData.observeForever { newTracks ->
            newTracks?.let {
                handleTracksUpdate(it)
            }
        }

    }

    private fun handleTracksUpdate(newTracks: List<MusicItem>) {
        if (newTracks.isNotEmpty()) {
            tracks.postValue(newTracks)

        }
    }

    fun getMediaPlayer(): MediaPlayer? {
        return musicPlayerService?.mediaPlayer
    }

    private fun startService() {
        val intent = Intent(this, MusicPlayerService::class.java)

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicPlayerService = binder.getService()
            checkVisibility()
            musicPlayerService?.let {
                it.mediaPlayer.let { mediaPlayer ->
                    val totalTime = mediaPlayer.duration
                    handler = android.os.Handler(Looper.getMainLooper())
                    updateProgress(totalTime)
                    cancelMusic()
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

    fun cancelMusic() {
        binding.imgCancel.setOnClickListener {
            sharedPreference.saveIsPlaying(false)
            sharedPreference.updateValue("PlayingMusic","")
            sharedPreference.updateValue("PlayingMusicUri","")
            sharedPreference.updateValue("PlayingMusicArtist","")
            setMusicPlayer(false)
            GsonHelper.serializeTracks(this, emptyList())
            musicPlayerService?.tracks?.postValue(emptyList())
            musicPlayerService?.stopMusic()
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
        musicPlayerService?.setTracks(tracks, position)
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


    private fun updateProgress(totalTime: Int) {
        musicPlayerService?.mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                val currentDuration = mediaPlayer.currentPosition
                val progress = (currentDuration.toFloat() / totalTime * 100).toInt()
                binding.progressBar.progress = progress
                handler.postDelayed({ updateProgress(totalTime) }, 100)
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
        var musicModel = MusicItem("", "", "", "")
        musicPlayerService?.let {
            musicModel = it.currentTrack()
        }
        return musicModel

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