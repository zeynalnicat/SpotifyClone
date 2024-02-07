package com.example.spotifyclone.ui.fragments.artist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.AlbumAdapter
import com.example.spotifyclone.databinding.FragmentArtistViewBinding
import com.example.spotifyclone.model.dto.Album


class ArtistViewFragment : Fragment() {
    private lateinit var binding: FragmentArtistViewBinding
    private var artistId = ""
    private var img = ""
    private var popularity = ""
    private var followers = ""
    private var genres = ""
    private lateinit var artistViewModel: ArtistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        artistViewModel = ViewModelProvider(this)[ArtistViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistViewBinding.inflate(inflater)
        getAllArguments()
        getAlbums()
        setNavigation()
        return binding.root

    }


    private fun getAllArguments() {
        arguments?.let {
            artistId = it.getString("artistId", "")
            img = it.getString("img", "")
            popularity = it.getString("popularity", "0")
            followers = it.getString("followers", "0")
            genres = it.getString("genres", "")
        }
    }

    private fun getAlbums() {
        artistViewModel.getAlbums(artistId)
        artistViewModel.albums.observe(viewLifecycleOwner) {
            Glide.with(binding.root)
                .load(img)
                .into(binding.imgAlbum)
            binding.txtAlbumName.text = it[0].artists[0].name
            binding.txtPopularity.text = popularity
            binding.txtFollowers.text = followers
            binding.txtGenres.text = genres
            val adapter = AlbumAdapter { findNavController().navigate(R.id.action_artistViewFragment_to_albumViewFragment,it) }
            val album = it.map { Album(it.images[0].url,it.id,it.name , emptyList()) }
            adapter.submitList(album)
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),2)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}