package com.example.spotifyclone.ui.fragments.playlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.PlaylistAdapter
import com.example.spotifyclone.databinding.FragmentAddPlaylistBinding
import com.example.spotifyclone.domain.model.dto.MusicItem

import com.example.spotifyclone.domain.model.dto.PlaylistModel
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.ui.fragments.playlist.viewmodel.AddPlaylistFactory
import com.example.spotifyclone.ui.fragments.playlist.viewmodel.AddPlaylistViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentAddPlaylistBinding

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var track: MusicItem? = null

    companion object {
        val selectedPlaylists = MutableLiveData<List<PlaylistModel>>()
    }

    private lateinit var adapter: PlaylistAdapter
    private val addPlaylistViewModel: AddPlaylistViewModel by viewModels {
        AddPlaylistFactory(
            firebaseAuth,
            firestore
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPlaylistBinding.inflate(inflater)
        setNavigation()
        search()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            track = it.getSerializable("track") as MusicItem
        }

        Log.d("track",track.toString())

        addPlaylistViewModel.playlists.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setAdapter(it.data)
                    binding.progressBar.visibility = View.GONE
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }


        selectedPlaylists.observe(viewLifecycleOwner) {
            when {
                it.isNotEmpty() -> {
                    binding.btnAdd.visibility = View.VISIBLE

                    binding.btnAdd.setOnClickListener { view ->
                        addPlaylistViewModel.addFirebase(it, track!!)

                    }

                }

                else -> {
                    binding.btnAdd.visibility = View.GONE
                }
            }
        }

        addPlaylistViewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (!it.data.contains(false)) {
                        findNavController().navigate(R.id.action_addPlaylistFragment_to_userLibraryFragment)
                    } else {
                        Toast.makeText(requireContext(), "Undefined Error", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {

                }

            }
        }

        addPlaylistViewModel.getPlaylists()
    }

    private fun setAdapter(playlists: List<PlaylistModel>) {
        adapter = PlaylistAdapter ({},{})
        adapter.submitList(playlists)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter

    }


    private fun setNavigation() {
        binding.navBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun search() {
        binding.edtSearch.doAfterTextChanged {
            addPlaylistViewModel.search(it.toString())
        }
    }
}