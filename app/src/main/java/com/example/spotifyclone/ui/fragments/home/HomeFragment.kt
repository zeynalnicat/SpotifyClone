package com.example.spotifyclone.ui.fragments.home

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.AlbumAdapter

import com.example.spotifyclone.adapters.TrackAdapter
import com.example.spotifyclone.databinding.FragmentHomeBinding
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.viewmodels.HomeViewModel


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        setBottom()

        setNewRelease()
        setTrySomethingElse()
        setAdapter()
        setTextHeader()


        return binding.root
    }

    private fun setAdapter() {

//
//        val recentAdapter =
//            RecentlyPlayedAdapter(recentTracks) { findNavController().navigate(R.id.action_homeFragment_to_albumViewFragment) }
//        binding.recyclerRecentlyPlayed.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerRecentlyPlayed.adapter = recentAdapter
//
//        val trySomethingAdapter =
//            TrackAdapter(somethingNewTracks) { findNavController().navigate(R.id.action_homeFragment_to_albumViewFragment) }
//        binding.recyclerTrySomething.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerTrySomething.adapter = trySomethingAdapter
//
    }

    private fun setBottom() {
        val activity = requireActivity() as? MainActivity
        activity?.setBottomNavigation(true)
    }

    private fun setTextHeader() {
        homeViewModel.setDateText()
        homeViewModel.date.observe(viewLifecycleOwner) {
            binding.txtGood.text = it
        }
    }

    private fun setNewRelease() {
        homeViewModel.getNewRelease()
        homeViewModel.newReleases.observe(viewLifecycleOwner) {
            val adapter =
                TrackAdapter {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_albumViewFragment,
                        it
                    )
                }
            adapter.submitList(it)
            binding.recyclerViewNewRelease.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewNewRelease.adapter = adapter

        }
    }

    private fun setTrySomethingElse() {
        homeViewModel.getRoomArtistAlbum(requireContext())
        homeViewModel.artists.observe(viewLifecycleOwner) {
            val adapter =
                AlbumAdapter { findNavController().navigate(R.id.action_homeFragment_to_artistViewFragment,it) }
            adapter.submitList(it)
            binding.recyclerTrySomething.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerTrySomething.adapter = adapter

        }
    }
}
