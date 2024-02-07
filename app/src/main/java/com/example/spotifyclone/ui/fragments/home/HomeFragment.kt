package com.example.spotifyclone.ui.fragments.home

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.ArtistAdapter
import com.example.spotifyclone.adapters.AlbumAdapter
import com.example.spotifyclone.databinding.FragmentHomeBinding
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.album.newrelease.Item
import com.example.spotifyclone.model.artist.Artist
import com.example.spotifyclone.model.dto.Album
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var roomDB: RoomDB
    private val homeViewModel: HomeViewModel by viewModels { HomeFactory(roomDB) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        setBottom()

        val activity = requireActivity() as MainActivity
        activity.checkVisibility()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomDB = RoomDB.accessDb(requireContext())!!
        setTextHeader()
        setNavigation()

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
                    binding.txtRecommend.visibility= View.VISIBLE
                    binding.recyclerViewRecommended.visibility = View.VISIBLE
                    setRecommended(it.data)
                }

                is Resource.Error -> {
                    binding.txtRecommend.visibility= View.GONE
                    binding.recyclerViewRecommended.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()
                }

                is Resource.Loading -> {}


            }
        }
        homeViewModel.getNewRelease()
        homeViewModel.getRoomArtistAlbum()
        homeViewModel.getPopularAlbums()
        homeViewModel.setRecommended()

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


    private fun setNavigation() {
        binding.imgSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
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
