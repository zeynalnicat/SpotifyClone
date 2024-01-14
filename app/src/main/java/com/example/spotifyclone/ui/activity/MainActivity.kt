package com.example.spotifyclone.ui.activity

import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ActivityMainBinding
import com.example.spotifyclone.ui.fragments.others.TrackViewFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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

    fun setBottomNavigation(visibility: Boolean) {
        if (visibility) {
            binding.bottomNav.visibility = View.VISIBLE
            binding.bottomNav.itemIconTintList = null;

        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }

    fun setMusicPlayer(visibility: Boolean){
        if(visibility){
            binding.musicPlayer.visibility = View.VISIBLE
        }else{
            binding.musicPlayer.visibility = View.GONE
        }
    }

    fun setMusicAttrs(imgUrl:String, name:String ){
        Glide.with(binding.root)
            .load(imgUrl)
            .into(binding.imgTrack)

        binding.txtMusicName.text = name
        var isPlayed = true
        binding.imgPause.setOnClickListener {
            isPlayed = !isPlayed
            binding.imgPause.setImageResource(if(isPlayed) R.drawable.icon_music_pause else R.drawable.icon_music_play)
        }

    }

    private fun setNavigation(){
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

}