package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.example.spotifyclone.databinding.FragmentLikedSongsBinding
import com.example.spotifyclone.network.db.RoomDB
import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.resource.Resource


class LikedSongsFragment : Fragment() {
    private lateinit var binding: FragmentLikedSongsBinding
    private lateinit var roomDB: RoomDB
    private val likedSongsViewModel: LikedSongsViewModel by viewModels { LikedSongsFactory(roomDB) }

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
        roomDB = RoomDB.accessDb(requireContext())!!

        likedSongsViewModel.songs.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setAdapter(it.data)
                    binding.txtSongs.text = "${it.data.size} song"
                }

                is Resource.Error -> {

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