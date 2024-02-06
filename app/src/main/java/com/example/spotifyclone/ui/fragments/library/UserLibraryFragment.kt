package com.example.spotifyclone.ui.fragments.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.adapters.PlaylistAdapter
import com.example.spotifyclone.databinding.FragmentUserLibraryBinding
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.dto.PlaylistModel
import com.example.spotifyclone.viewmodels.UserLibraryViewModel
import com.example.spotifyclone.viewmodels.factories.UserLibraryFactory

class UserLibraryFragment : Fragment() {
    private lateinit var binding: FragmentUserLibraryBinding
    private lateinit var roomDB: RoomDB
    private val userLibraryViewModel: UserLibraryViewModel by viewModels { UserLibraryFactory(roomDB) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserLibraryBinding.inflate(inflater)
        setNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        roomDB = RoomDB.accessDb(requireContext())!!
        userLibraryViewModel.getPlaylists()
        userLibraryViewModel.playlists.observe(viewLifecycleOwner){
            setAdapter(it)
        }

    }

    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setAdapter(list:List<PlaylistModel>){
        val adapter = PlaylistAdapter()
        adapter.submitList(list)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        binding.recyclerView.adapter = adapter

    }

}