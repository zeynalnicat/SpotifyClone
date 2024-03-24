package com.example.spotifyclone.ui.fragments.library

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.LibraryAlbumAdapter
import com.example.spotifyclone.databinding.FragmentLibraryBinding
import com.example.spotifyclone.domain.model.dto.Album

import com.example.spotifyclone.data.network.api.AlbumApi
import com.example.spotifyclone.domain.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding


    @Inject
    lateinit var albumApi: AlbumApi

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var deezerAlbumApi: com.example.spotifyclone.data.network.api.deezer.AlbumApi

    private val libraryViewModel: LibraryViewModel by viewModels {
        LibraryFactor(
            albumApi,
            firebaseAuth,
            firestore,
            deezerAlbumApi
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater)
        setLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryViewModel.getFromDB()
        libraryViewModel.albumIds.observe(viewLifecycleOwner) {
            libraryViewModel.getAlbumsFromApi(it)
        }

        libraryViewModel.count.observe(viewLifecycleOwner) {
            binding.txtNumberSongs.text = it.toString()
        }

        libraryViewModel.likedAlbumsFirestore.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.recyclerFirebase.visibility = View.VISIBLE
                    setAdapterFirebase(it.data)
                }

                is Resource.Error -> {
                    binding.recyclerFirebase.visibility = View.GONE
                }

                is Resource.Loading -> {

                }
            }
        }

        libraryViewModel.deezerAlbums.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.recyclerFirebase.visibility = View.VISIBLE
                    setAdapterDeezer(it.data)
                }

                is Resource.Error -> {

                }

                is Resource.Loading -> {

                }
            }
        }




        libraryViewModel.getAlbumFirestore()
        libraryViewModel.getDeezerAlbum()
        libraryViewModel.setSize()
        setAdapter()
        setNavigation()
        setDrawer()
    }


    private fun setNavigation() {

        binding.viewProfile.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_userLibraryFragment)
        }
        binding.viewSetting.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_settingsFragment)
        }
        binding.imgAdd.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }
        binding.viewLikedSongs.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_likedSongsFragment)
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

    private fun setDrawer() {
        binding.imgProfileAccount.setOnClickListener {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            } else {
                binding.drawer.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setAdapter() {
        libraryViewModel.likedAlbums.observe(viewLifecycleOwner) {
            val adapter = LibraryAlbumAdapter {
                findNavController().navigate(
                    R.id.action_libraryFragment_to_albumViewFragment,
                    it
                )
            }
            val albums =
                it.map {
                    com.example.spotifyclone.domain.model.dto.Album(
                        it.images[0].url,
                        it.id,
                        it.name,
                        emptyList(),
                        isLibrary = true
                    )
                }
            adapter.submitList(albums)
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun setAdapterDeezer(data: List<com.example.spotifyclone.domain.model.dto.Album>) {
        val adapter = LibraryAlbumAdapter {
            findNavController().navigate(
                R.id.action_libraryFragment_to_albumViewFragment,
                it
            )
        }
        val albumsModel =
            data.map {
                com.example.spotifyclone.domain.model.dto.Album(
                    it.coverImg,
                    it.id,
                    it.name,
                    it.tracks,
                    true,
                    true,
                    isDeezer = true
                )
            }
        adapter.submitList(albumsModel)
        binding.recyclerDeezer.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerDeezer.adapter = adapter
    }


    private fun setAdapterFirebase(albums: List<com.example.spotifyclone.domain.model.dto.Album>) {
        val adapter = LibraryAlbumAdapter {
            findNavController().navigate(
                R.id.action_libraryFragment_to_albumViewFragment,
                it
            )
        }
        val albumsModel = albums.map {
            com.example.spotifyclone.domain.model.dto.Album(
                it.coverImg,
                it.id,
                it.name,
                it.tracks,
                true,
                true
            )
        }
        adapter.submitList(albumsModel)
        binding.recyclerFirebase.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerFirebase.adapter = adapter
    }
}