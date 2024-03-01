package com.example.spotifyclone.ui.fragments.home

import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.ArtistAdapter
import com.example.spotifyclone.ui.adapters.AlbumAdapter
import com.example.spotifyclone.databinding.FragmentHomeBinding
import com.example.spotifyclone.databinding.FragmentHomeGeneralBinding

import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.model.dto.Album
import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.network.deezer.TrackApi
import com.example.spotifyclone.network.retrofit.TokenRefresher
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.network.retrofit.api.ArtistsApi
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.adapters.LibraryAlbumAdapter
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


@AndroidEntryPoint

class FragmentHomeGeneral : Fragment() {
    private lateinit var binding: FragmentHomeGeneralBinding

    @Inject
    lateinit var artistsApi: ArtistsApi

    @Inject
    lateinit var albumApi: AlbumApi

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var tokenRefresher: TokenRefresher

    private lateinit var trackApi: TrackApi


    private val homeViewModel: HomeViewModel by viewModels {
        HomeFactory(
            albumApi,
            artistsApi,
            firestore,
            firebaseAuth,
            tokenRefresher,
            trackApi
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeGeneralBinding.inflate(inflater)
        setBottom()

        val activity = requireActivity() as MainActivity
        activity.checkVisibility()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        trackApi = retrofit.create(TrackApi::class.java)


        homeViewModel.newReleases.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.txtNewRelease.visibility = View.VISIBLE
                    binding.recyclerViewNewRelease.visibility = View.VISIBLE
                    setNewRelease(it.data)
                }

                is Resource.Error -> {
                    binding.txtNewRelease.visibility = View.GONE
                    binding.recyclerViewNewRelease.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()

                }

                is Resource.Loading -> {}
            }

        }

        homeViewModel.artists.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setTrySomethingElse(it.data)
                    binding.recyclerTrySomething.visibility = View.VISIBLE
                    binding.txtTry.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    binding.recyclerTrySomething.visibility = View.GONE
                    binding.txtTry.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()
                }

                is Resource.Loading -> {}


            }
        }

        homeViewModel.topMusics.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setTopMusics(it.data)
                }
                is Resource.Error -> {

                }

                is Resource.Loading -> {}

            }
        }

        homeViewModel.getTopMusics()

        homeViewModel.popularAlbums.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setPopularAlbums(it.data)
                    binding.recyclerViewPopular.visibility = View.VISIBLE
                    binding.txtPopularAlbums.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    binding.recyclerViewPopular.visibility = View.GONE
                    binding.txtPopularAlbums.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()
                }

                is Resource.Loading -> {}


            }
        }

        homeViewModel.recommended.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.txtRecommend.visibility = View.VISIBLE
                    binding.recyclerViewRecommended.visibility = View.VISIBLE
                    setRecommended(it.data)
                }

                is Resource.Error -> {
                    binding.txtRecommend.visibility = View.GONE
                    binding.recyclerViewRecommended.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()
                }

                is Resource.Loading -> {}


            }
        }
        homeViewModel.getNewRelease()
        homeViewModel.getArtist()
        homeViewModel.getPopularAlbums()
        homeViewModel.setRecommended()

    }

    private fun setTopMusics(data: List<MusicItem>) {
        val adapter = LikedSongsAdapter(saveSharedPreference = { a, b -> println() })
        val model = data.map { LikedSongs(it.name,it.artist,it.img,it.trackUri, isTopTracks = true) }
        adapter.submitList(model)

        binding.recyclerTopMusics.layoutManager = GridLayoutManager(requireContext(),2)
        binding.recyclerTopMusics.adapter =adapter

    }


    private fun setBottom() {
        val activity = requireActivity() as? MainActivity
        activity?.setBottomNavigation(true)
    }


    private fun setNewRelease(list: List<Item>) {

        val adapter =
            AlbumAdapter {
                findNavController().navigate(
                    R.id.action_homeFragment_to_albumViewFragment,
                    it
                )
            }
        val albums = list.map { Album(it.images[0].url, it.id, it.name, emptyList()) }
        adapter.submitList(albums)
        binding.recyclerViewNewRelease.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewNewRelease.adapter = adapter

    }


    private fun setTrySomethingElse(list: List<Artist>) {

        val adapter =
            ArtistAdapter {
                findNavController().navigate(
                    R.id.action_homeFragment_to_artistViewFragment,
                    it
                )
            }
        adapter.submitList(list)
        binding.recyclerTrySomething.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerTrySomething.adapter = adapter

    }


    private fun setPopularAlbums(list: List<com.example.spotifyclone.model.album.popularalbums.Album>) {

        val adapter =
            AlbumAdapter {
                findNavController().navigate(
                    R.id.action_homeFragment_to_albumViewFragment,
                    it
                )
            }
        val albums = list.map { Album(it.images[0].url, it.id, it.name, emptyList()) }
        adapter.submitList(albums)
        binding.recyclerViewPopular.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewPopular.adapter = adapter
    }


    private fun setRecommended(list: List<Album>) {
        val adapter = AlbumAdapter {
            findNavController().navigate(
                R.id.action_homeFragment_to_albumViewFragment,
                it
            )
        }
        adapter.submitList(list)
        binding.recyclerViewRecommended.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewRecommended.adapter = adapter


    }


}
