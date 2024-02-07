package com.example.spotifyclone.ui.fragments.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.adapters.PlaylistAdapter
import com.example.spotifyclone.databinding.FragmentAddPlaylistBinding
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.dto.PlaylistModel
import com.example.spotifyclone.resource.Resource

class AddPlaylistFragment : Fragment() {
    private lateinit var binding:FragmentAddPlaylistBinding
    private lateinit var roomDB: RoomDB
    private val addPlaylistViewModel : AddPlaylistViewModel by viewModels { AddPlaylistFactory(roomDB) }

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
        roomDB = RoomDB.accessDb(requireContext())!!
        addPlaylistViewModel.playlists.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    setAdapter(it.data)
                }
                is Resource.Error ->{
                    Toast.makeText(requireContext(),it.exception.message,Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {

                }
            }
        }
        addPlaylistViewModel.getPlaylists()
    }

    private fun setAdapter(playlists:List<PlaylistModel>){
        val adapter = PlaylistAdapter()
        adapter.submitList(playlists)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        binding.recyclerView.adapter = adapter
    }


    private fun setNavigation(){
        binding.navBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }
}