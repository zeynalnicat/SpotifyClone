package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.example.spotifyclone.databinding.FragmentLikedSongsBinding

import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LikedSongsFragment : Fragment() {
    private lateinit var binding: FragmentLikedSongsBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val likedSongsViewModel: LikedSongsViewModel by viewModels { LikedSongsFactory(firestore,firebaseAuth) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikedSongsBinding.inflate(inflater)
        setNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        likedSongsViewModel.songs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setAdapter(it.data)
                    binding.txtSongs.text = "${it.data.size} song"
                }

                is Resource.Error -> {
                      Toast.makeText(requireContext(),it.exception.message,Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {

                }


            }
        }

        binding.edtSearch.doAfterTextChanged {
            val query = it.toString()
            likedSongsViewModel.search(query)
        }

        likedSongsViewModel.getSongs()
    }


    private fun setNavigation() {
        binding.navBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setAdapter(songs: List<LikedSongs>) {
        val adapter = LikedSongsAdapter()
        adapter.submitList(songs)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter
    }


}