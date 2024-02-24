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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.ArtistAdapter
import com.example.spotifyclone.ui.adapters.AlbumAdapter
import com.example.spotifyclone.databinding.FragmentHomeBinding

import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.model.dto.Album
import com.example.spotifyclone.network.retrofit.TokenRefresher
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.network.retrofit.api.ArtistsApi
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

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


    private val homeViewModel: HomeViewModel by viewModels {
        HomeFactory(
            albumApi,
            artistsApi,
            firestore,
            firebaseAuth,
            tokenRefresher
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        setBottom()
        setLayout()
        setNavigation()

        val activity = requireActivity() as MainActivity
        activity.checkVisibility()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextHeader()
        setDrawer()

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

    private fun setNavigation() {
        binding.viewProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_userLibraryFragment)
        }

        binding.viewSetting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    private fun setLayout() {
        val userRef = firestore.collection("users")
        val query = userRef.whereEqualTo("userId", firebaseAuth.currentUser?.uid)
        try {
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val gender = querySnapshot.documents[0].getString("gender")
                        val name = querySnapshot.documents[0].getString("username")
                        val image = querySnapshot.documents[0].getString("img")

                        binding.txtNameDrawer.text = name ?: "N/A"
                        if (image?.isEmpty() == true) {
                            binding.imgProfileAccount.setImageResource(if (gender == "Men") R.drawable.man_icon else R.drawable.woman_icon)
                            binding.imgProfileDrawer.setImageResource(if (gender == "Men") R.drawable.man_icon else R.drawable.woman_icon)
                        } else {
                            Glide.with(binding.root)
                                .load(image.toString())
                                .into(binding.imgProfileAccount)

                            Glide.with(binding.root)
                                .load(image.toString())
                                .into(binding.imgProfileDrawer)
                        }
                    } else {
                        binding.txtNameDrawer.text = "N/A"
                    }
                }
                .addOnFailureListener { exception ->
                    binding.txtNameDrawer.text = "N/A"
                    Log.e("Firestore", "Error retrieving user data: ${exception.message}")
                }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

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

    private fun setDrawer() {
        binding.imgProfileAccount.setOnClickListener {
            if (binding.drawer2.isDrawerOpen(GravityCompat.START)) {
                binding.drawer2.closeDrawer(GravityCompat.START)
            } else {
                binding.drawer2.openDrawer(GravityCompat.START)
            }
        }
    }
}
