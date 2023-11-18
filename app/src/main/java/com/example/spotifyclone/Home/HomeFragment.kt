package com.example.spotifyclone.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.spotifyclone.Adapters.RecentlyPlayedAdapter
import com.example.spotifyclone.Adapters.TrackAdapter
import com.example.spotifyclone.PlayedTracks
import com.example.spotifyclone.R
import com.example.spotifyclone.Tracks
import com.example.spotifyclone.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
   private lateinit var binding : FragmentHomeBinding
   private lateinit var recentTracks: MutableList<PlayedTracks>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        recentTracks = mutableListOf(
            PlayedTracks(R.drawable.billie, "Billie Eilish"),
            PlayedTracks(R.drawable.ariaa, "Ariana Grande",true),
            PlayedTracks(R.drawable.billie, "Billie Eilish"),
            PlayedTracks(R.drawable.ariaa, "Ariana Grande"),
            PlayedTracks(R.drawable.billie, "Billie Eilish",true),
            PlayedTracks(R.drawable.ariaa, "Ariana Grande",true), PlayedTracks(R.drawable.billie, "Billie Eilish"),
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

        val recentAdapter = RecentlyPlayedAdapter(recentTracks)
        binding.recyclerRecentlyPlayed.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerRecentlyPlayed.adapter = recentAdapter

        val trySomethingAdapter = TrackAdapter(somethingNewTracks)
        binding.recyclerTrySomething.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerTrySomething.adapter = trySomethingAdapter

        return binding.root
    }

}