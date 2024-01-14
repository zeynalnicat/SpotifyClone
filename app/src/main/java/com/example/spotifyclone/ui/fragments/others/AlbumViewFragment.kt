package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.adapters.SingleAlbumTracksAdapter
import com.example.spotifyclone.databinding.FragmentAlbumViewBinding
import com.example.spotifyclone.model.album.singlealbum.Artist
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.viewmodels.AlbumViewModel

class AlbumViewFragment : Fragment() {
    private lateinit var binding: FragmentAlbumViewBinding
    private var albumId = ""
    private lateinit var albumViewModel: AlbumViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumViewBinding.inflate(inflater)
        setNavigation()
        getAlbumId()
        albumViewModel = ViewModelProvider(this)[AlbumViewModel::class.java]
        getAlbum()
        return binding.root
    }


    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun getAlbumId() {
        arguments?.let {
            albumId = it.getString("albumId", "")
        }
    }

    private fun getAlbum() {
        albumViewModel.getAlbum(albumId)
        albumViewModel.album.observe(viewLifecycleOwner) {
            val artistNames = it.artists.joinToString { artist: Artist -> artist.name + " " }
            binding.txtArtistName.text = artistNames
            binding.txtAlbumYear.text = it.release_date
            binding.txtAlbumName.text = it.name
            Glide.with(binding.root)
                .load(it.images[0].url)
                .into(binding.imgAlbum)
            Glide.with(binding.root)
                .load(it.images[1].url)
                .into(binding.imgArtist)
            setAdapter(it.images[0].url, it.tracks.items)
        }
    }

    private fun setAdapter(
        img: String,
        tracks: List<com.example.spotifyclone.model.album.singlealbum.Item>
    ) {
        val adapter = SingleAlbumTracksAdapter(img,
            { setMusicTrack() },
            { url, name -> setMusicAttrs(url, name) })
        adapter.submitList(tracks)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter

    }

    private fun setMusicTrack() {
        val activity = requireActivity() as MainActivity
        activity.setMusicPlayer(true)
    }

    private fun setMusicAttrs(url: String, name: String) {
        val activity = requireActivity() as MainActivity
        activity.setMusicAttrs(url, name)
    }

}