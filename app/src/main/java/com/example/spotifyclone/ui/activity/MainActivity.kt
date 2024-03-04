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
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: android.os.Handler
    private var musicPlayerService: MusicPlayerService? = null
    private lateinit var musicPlayerViewModel: MusicPlayerViewModel

    private lateinit var repository: MusicRepository
    private lateinit var sharedPreference: SharedPreference
    private var position = 0

    override fun attachBaseContext(newBase: Context) {
        val updatedContext = updateLocale(newBase)
        super.attachBaseContext(updatedContext)
    }

    private fun updateLocale(context: Context): Context {
        val savedLanguage = LanguageHelper.getSavedLanguage(context)
        return LanguageHelper.setLocale(context, savedLanguage)
    }
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


            musicPlayerViewModel.tracks.observe(this@MainActivity) { tracks ->
                val intent = Intent(this@MainActivity, MusicPlayerService::class.java)
                intent.action = MusicPlayerService.ACTION_SET_TRACKS
                intent.putExtra(MusicPlayerService.EXTRA_TRACKS_JSON, ArrayList(tracks))
                this@MainActivity.startService(intent)
            }

            musicPlayerViewModel.selectedTrackPosition.observe(this@MainActivity) { position ->
                this@MainActivity.position = position
                setMusicPlayer(true)
            }



        musicPlayerViewModel.selectedTrackPosition.observe(this){
            musicPlayerService?.setPosition(it)
        }

        repository.loadTracks()

        repository.tracksLiveData.observeForever { newTracks ->
            newTracks?.let {
                handleTracksUpdate(it)
            }
        }


    }

    private fun handleTracksUpdate(newTracks: List<MusicItem>) {
        if (newTracks.isNotEmpty()) {
            musicPlayerViewModel.setTracks(newTracks)

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

            handleMusic()
            updateProgress()
            checkVisibility()
            cancelMusic()

        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    fun handleMusic() {
        musicPlayerService?.let {
            val mediaPlayer = it.mediaPlayer
            cancelMusic()



            binding.imgPause.setOnClickListener { view ->
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
            sharedPreference.saveIsPlaying(false)
            sharedPreference.updateValue("PlayingMusic", "")
            sharedPreference.updateValue("PlayingMusicUri", "")
            sharedPreference.updateValue("PlayingMusicArtist", "")
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

    fun getMusicPlayerService(): MusicPlayerService? {
        return musicPlayerService
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


}