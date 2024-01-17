package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentTrackViewBinding
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.activity.MainActivity


class TrackViewFragment : Fragment() {
    private lateinit var binding: FragmentTrackViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackViewBinding.inflate(inflater)
        val activity = activity as MainActivity
        activity.setBottomNavigation(false)
        setNavigation()
        setLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val activity = activity as MainActivity
                activity.setBottomNavigation(true)
                activity.setMusicPlayer(true)
                activity.supportFragmentManager.popBackStack()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)
    }

    private fun setNavigation() {
        binding.imgShrink.setOnClickListener {
            val activity = activity as MainActivity
            activity.setMusicPlayer(true)
            activity.setBottomNavigation(true)
            activity.supportFragmentManager.popBackStack()
        }
    }

    private fun setLayout() {
        val sharedPreference = SharedPreference(requireContext())
        var isPlayed = true
        binding.imgPause.setOnClickListener {
            isPlayed = !isPlayed
            binding.imgPause.setImageResource(if (isPlayed) R.drawable.icon_music_view_pause else R.drawable.icon_music_view_resume)
        }
        val musicName = sharedPreference.getValue("PlayingMusic", "")
        val artistName = sharedPreference.getValue("PlayingMusicArtist", "")
        val musicImg = sharedPreference.getValue("PlayingMusicImg", "")

        binding.txtTrackName.text = musicName
        binding.txtTrackHeader.text = musicName
        binding.txtArtistName.text = artistName
        Glide.with(binding.root)
            .load(musicImg)
            .into(binding.imgTrack)

    }


}