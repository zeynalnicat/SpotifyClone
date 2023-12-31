package com.example.spotifyclone.ui.activity

import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    fun setBottomNavigation(isSet: Boolean) {
        binding.bottomNav.visibility = if (isSet) View.VISIBLE else View.GONE
    }

}