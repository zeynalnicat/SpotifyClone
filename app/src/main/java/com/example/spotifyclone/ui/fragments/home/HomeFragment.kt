package com.example.spotifyclone.ui.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.spotifyclone.model.PlayedTracks
import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.RecentlyPlayedAdapter
import com.example.spotifyclone.adapters.TrackAdapter
import com.example.spotifyclone.model.Tracks
import com.example.spotifyclone.databinding.FragmentHomeBinding
import com.example.spotifyclone.ui.activity.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recentTracks: MutableList<PlayedTracks>
    private var greeting: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        setBottom()
        setAdapter()
        setTextHeader()
        setNavigation()

        return binding.root
    }

    private fun setAdapter() {
        recentTracks = mutableListOf(
            PlayedTracks(R.drawable.billie, "Billie Eilish"),
            PlayedTracks(R.drawable.ariaa, "Ariana Grande", true),
            PlayedTracks(R.drawable.billie, "Billie Eilish"),
            PlayedTracks(R.drawable.ariaa, "Ariana Grande"),
            PlayedTracks(R.drawable.billie, "Billie Eilish", true),
            PlayedTracks(R.drawable.ariaa, "Ariana Grande", true),
            PlayedTracks(R.drawable.billie, "Billie Eilish"),
            PlayedTracks(R.drawable.ariaa, "Ariana Grande"),


            )

        val somethingNewTracks = mutableListOf(
            Tracks(R.drawable.kanye, "Kanye West"),
            Tracks(R.drawable.kanye, "Kanye West"),
            Tracks(R.drawable.kanye, "Kanye West"),
            Tracks(R.drawable.kanye, "Kanye West"),
            Tracks(R.drawable.kanye, "Kanye West"),
            Tracks(R.drawable.kanye, "Kanye West"),
            Tracks(R.drawable.kanye, "Kanye West"),

            )

        val recentAdapter = RecentlyPlayedAdapter(recentTracks){findNavController().navigate(R.id.action_homeFragment_to_albumViewFragment)}
        binding.recyclerRecentlyPlayed.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerRecentlyPlayed.adapter = recentAdapter

        val trySomethingAdapter = TrackAdapter(somethingNewTracks){findNavController().navigate(R.id.action_homeFragment_to_albumViewFragment)}
        binding.recyclerTrySomething.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerTrySomething.adapter = trySomethingAdapter
    }

    private fun setBottom() {
        val activity = requireActivity() as? MainActivity
        activity?.setBottomNavigation(true)
    }

    private fun setTextHeader() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("HH", Locale.getDefault())
        val hour = dateFormat.format(currentDate).toInt()

        greeting = when (hour) {
            in 6..11 -> "Good morning!"
            in 12..15 -> "Good afternoon!"
            in 16..20 -> "Good evening!"
            else -> "Good night"
        }
        binding.txtGood.text = greeting
    }

    private fun setNavigation(){
        binding.imgSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }
}
