package com.example.spotifyclone.ui.activity

import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ActivityMainBinding
import com.example.spotifyclone.musicplayer.MusicPlayer
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.fragments.others.TrackViewFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var totalTime = 0

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

        MusicPlayer.initialize(this,musicUri)
        val music = MusicPlayer.getMediaPlayer()
        music?.start()

            binding.imgPause.setOnClickListener {
                if(music?.isPlaying==true){
                    music.pause()
                    binding.imgPause.setImageResource(R.drawable.icon_music_play)
                }else{
                    music?.start()
                    binding.imgPause.setImageResource(R.drawable.icon_music_pause)
                }
            }



        Glide.with(binding.root)
            .load(musicImg)
            .into(binding.imgTrack)


        binding.txtMusicName.text = musicName



    }

    private fun setNavigation() {
        binding.musicPlayer.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
            val trackViewFragment = TrackViewFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, trackViewFragment)
                .addToBackStack(null)
                .commit()
            setMusicPlayer(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayer.releaseMediaPlayer()
    }

}