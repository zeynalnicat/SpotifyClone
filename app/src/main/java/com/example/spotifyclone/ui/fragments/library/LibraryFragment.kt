package com.example.spotifyclone.ui.fragments.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.LibraryAlbumAdapter
import com.example.spotifyclone.databinding.FragmentLibraryBinding
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.model.dto.LibraryAlbum
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var roomDB: RoomDB


    @Inject
    lateinit var albumApi: AlbumApi
    private val libraryViewModel: LibraryViewModel by viewModels { LibraryFactor(roomDB, albumApi) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomDB = RoomDB.accessDb(requireContext())!!

        libraryViewModel.getFromDB()
        libraryViewModel.roomAlbums.observe(viewLifecycleOwner) {
            libraryViewModel.getAlbumsFromApi(it)
        }

        libraryViewModel.count.observe(viewLifecycleOwner) {
            binding.txtNumberSongs.text = it.toString()
        }

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
            val albums = it.map { LibraryAlbum(it.id, it.name, it.images[0].url) }
            adapter.submitList(albums)
            binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
            binding.recyclerView.adapter = adapter
        }
    }
}