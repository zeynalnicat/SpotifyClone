package com.example.spotifyclone.ui.fragments.playlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spotifyclone.ui.adapters.PlaylistAdapter
import com.example.spotifyclone.databinding.FragmentAddPlaylistBinding

import com.example.spotifyclone.model.dto.PlaylistModel
import com.example.spotifyclone.resource.Resource
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
    
//    companion object{
//        val selectedPlaylists = mutab
//    }

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
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addPlaylistViewModel.playlists.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setAdapter(it.data)
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
        adapter = PlaylistAdapter{}
        adapter.submitList(playlists)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {

                    val selectedPlaylists = adapter.getSelectedPlaylists()
                    Log.e("list", selectedPlaylists.toString())
                    if (selectedPlaylists.isNotEmpty()) {
                        binding.btnAdd.visibility = View.VISIBLE
                    } else {
                        binding.btnAdd.visibility = View.GONE
                    }
                }

        })
    }


    private fun setNavigation() {
        binding.navBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}