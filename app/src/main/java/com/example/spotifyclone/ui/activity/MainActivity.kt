package com.example.spotifyclone.ui.activity

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ActivityMainBinding
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.model.firebase.Tracks
import com.example.spotifyclone.musicplayer.MusicPlayer
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.fragments.track.TrackViewFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: android.os.Handler
    private var totalTime: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        setNavigation()


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

        MusicPlayer.initialize(this, uri)
        val music = MusicPlayer.getMediaPlayer()

        music?.let {
            it.start()
            totalTime = it.duration
            handler = android.os.Handler(Looper.getMainLooper())
            updateProgress(it)


            binding.imgPause.setOnClickListener { view ->
                if (it.isPlaying) {
                    it.pause()
                    binding.imgPause.setImageResource(R.drawable.icon_music_play)
                } else {
                    it.start()
                    binding.imgPause.setImageResource(R.drawable.icon_music_pause)
                }
            }

        }

        Glide.with(binding.root)
            .load(img)
            .into(binding.imgTrack)


        binding.txtMusicName.text = name
    }

    fun initializeMusic(musicUri: String) {
        MusicPlayer.getMediaPlayer()?.setDataSource(this, Uri.parse(musicUri))
    }

    private fun updateProgress(music: MediaPlayer?) {
        music?.let {
            handler.postDelayed({

                val currentDuration = it.currentPosition
                val progress = (currentDuration.toFloat() / totalTime * 100).toInt()
                binding.progressBar.progress = progress

                updateProgress(it)
            }, 100)
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


    fun playTracks(list: List<MusicItem>) {

    }


}