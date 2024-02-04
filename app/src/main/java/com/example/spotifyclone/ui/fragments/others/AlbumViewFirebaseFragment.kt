package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentAlbumViewBinding

class AlbumViewFirebaseFragment : Fragment() {
    private lateinit var binding: FragmentAlbumViewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumViewBinding.inflate(inflater)
        return binding.root
    }


}