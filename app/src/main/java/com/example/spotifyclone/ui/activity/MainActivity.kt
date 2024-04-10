package com.example.spotifyclone.ui.activity

import LanguageHelper
import SwipeGestureDetector
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.databinding.ActivityMainBinding
import com.example.spotifyclone.domain.model.dto.MusicItem
import com.example.spotifyclone.service.MusicPlayerService
import com.example.spotifyclone.domain.MusicRepository
import com.example.spotifyclone.ui.activity.viewmodel.MusicPlayerViewModel
import com.example.spotifyclone.ui.fragments.track.TrackViewFragment
import com.example.spotifyclone.util.GsonHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SwipeGestureDetector.OnSwipeListener,  MusicPlayerService.MusicPlayerCallback {
    private lateinit var binding: ActivityMainBinding
    private val musicPlayerViewModel : MusicPlayerViewModel by viewModels()


    @Inject
    lateinit var repository: MusicRepository

    private lateinit var sharedPreference: SharedPreference
    private var position = 0
    private var isServiceBound = false

    companion object {
        private var musicPlayerService: MusicPlayerService? = null

        fun getMusicPlayerService(): MusicPlayerService? {
            return musicPlayerService
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val updatedContext = updateLocale(newBase)
        super.attachBaseContext(updatedContext)
    }

    private fun updateLocale(context: Context): Context {
        val savedLanguage = LanguageHelper.getSavedLanguage(context)
        return LanguageHelper.setLocale(context, savedLanguage)
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicPlayerService = binder.getService()
            musicPlayerService?.setMusicPlayerCallback(this@MainActivity)
            handleMusic()
            updateProgress()
            checkVisibility()
            isServiceBound = true

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val swipeGestureDetector = SwipeGestureDetector(this, binding.musicPlayer, this)
        sharedPreference = SharedPreference(this)
        repository = MusicRepository(applicationContext)
        binding.musicPlayer.setOnTouchListener(swipeGestureDetector)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        setNavigation()
        repository.loadTracks()
        observeAll()

    }
    private fun handleTracksUpdate(newTracks: List<MusicItem>) {
        if (newTracks.isNotEmpty()) {
            musicPlayerViewModel.setTracks(newTracks)
        }
    }
    fun getMediaPlayer(): MediaPlayer? {
        return musicPlayerService?.mediaPlayer
    }
    private fun observeAll() {
        repository.tracksLiveData.observeForever { newTracks ->
            newTracks?.let {
                handleTracksUpdate(it)
            }
        }
        musicPlayerViewModel.current.observe(this) {
            setMusicLayout(it.name, it.img, it.trackUri)
        }

        musicPlayerViewModel.tracks.observe(this@MainActivity) { tracks ->
            val intent = Intent(this@MainActivity, MusicPlayerService::class.java)
            intent.action = MusicPlayerService.ACTION_SET_TRACKS
            intent.putExtra(MusicPlayerService.EXTRA_TRACKS_JSON, ArrayList(tracks))

            if (!isServiceBound) {
                startTheService(intent)
                isServiceBound = true
            } else {
                musicPlayerService?.setTracks(tracks, position)
            }
        }
        musicPlayerViewModel.selectedTrackPosition.observe(this@MainActivity) { position ->
            this@MainActivity.position = position
            musicPlayerService?.setPosition(position)
            setMusicPlayer(true)
        }

    }

    private fun startTheService(musicIntent: Intent) {
        bindService(musicIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        startForegroundService(musicIntent)
    }

    fun handleMusic() {
        musicPlayerService?.let {
            val mediaPlayer = it.mediaPlayer
            cancelMusic()

            binding.imgPause.setOnClickListener { view ->
                view.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(300)
                    .alpha(0.6f)
                    .withEndAction {
                        view.scaleX = 1.0f
                        view.scaleY = 1.0f
                        view.alpha = 1.0f
                    }
                    .start()
                if (mediaPlayer.isPlaying) {
                    it.pauseMusic()
                    binding.imgPause.setImageResource(R.drawable.icon_music_play)
                } else {
                    it.startMusic()
                    binding.imgPause.setImageResource(R.drawable.icon_music_pause)
                }
            }
        }
    }

    fun cancelMusic() {

        binding.imgCancel.setOnClickListener {
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(300)
                .alpha(0.6f)
                .withEndAction {
                    it.scaleX = 1.0f
                    it.scaleY = 1.0f
                    it.alpha = 1.0f
                }

            removeMusic()
        }

    }


    fun removeMusic(){
        sharedPreference.saveIsPlaying(false)
        sharedPreference.updateValue("PlayingMusic", "")
        sharedPreference.updateValue("PlayingMusicUri", "")
        sharedPreference.updateValue("PlayingMusicArtist", "")
        sharedPreference.updateValue("PlayingMusicImg", "")
        setMusicPlayer(false)
        GsonHelper.serializeTracks(this, emptyList())
        musicPlayerService?.tracks?.value = emptyList()
        musicPlayerService?.stopMusic()
        musicPlayerService?.removeNotification()
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


    fun setMusicAttrs() {
        val sharedPreference = SharedPreference(this)
        val musicName = sharedPreference.getValue("PlayingMusic", "")
        val musicImg = sharedPreference.getValue("PlayingMusicImg", "")
        val musicUri = sharedPreference.getValue("PlayingMusicUri", "")

        setMusicLayout(musicName, musicImg, musicUri)

    }

    private fun setMusicLayout(name: String, img: String, uri: String) {

        if (!isDestroyed) {
            Glide.with(this)
                .load(img)
                .into(binding.imgTrack)
        }


        binding.txtMusicName.text = name
    }


    private fun updateProgress() {
        try {
            musicPlayerService?.let {
                val mediaPlayer = it.mediaPlayer

                binding.progressBar.postDelayed(object : Runnable {
                    override fun run() {
                        val totalTime = mediaPlayer.duration
                        binding.progressBar.progress =
                            (mediaPlayer.currentPosition.toFloat() / totalTime * 100).toInt()
                        binding.progressBar.postDelayed(this, 1000)

                    }
                }, 0)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ProgressBar", "Error updating progress: ${e.message}")

        }
    }


    private fun setNavigation() {
        binding.musicPlayer.setOnClickListener {
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            val trackViewFragment = TrackViewFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, trackViewFragment)
                .addToBackStack(null)
                .setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
                .commit()
            setMusicPlayer(false)
        }
    }


    fun getCurrentTrack() {
        musicPlayerService?.let {
            musicPlayerViewModel.setCurrentMusic(it.currentTrack())
        }
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


    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    override fun onSwipeRight() {
        musicPlayerService?.prevSong()
    }

    override fun onSwipeLeft() {
        musicPlayerService?.nextSong()
    }

    override fun onMusicChanged(music: MusicItem) {
        setMusicLayout(music.name, music.img, music.trackUri)
        musicPlayerViewModel.setCurrentMusic(music)
    }


    fun stopMusicService(){
        removeMusic()
        stopService(Intent(this,MusicPlayerService::class.java))

    }

}