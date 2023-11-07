package com.example.spotifyclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.spotifyclone.databinding.ActivityHomeBinding
import com.example.spotifyclone.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val navHostFragment = supportFragmentManager.findFragmentById(androidx.navigation.fragment.R.id.nav_host_fragment_container) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNav,navHostFragment.navController)
        setContentView(binding.root)
    }
}