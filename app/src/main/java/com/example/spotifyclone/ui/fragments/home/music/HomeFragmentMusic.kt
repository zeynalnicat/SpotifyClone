package com.example.spotifyclone.ui.fragments.home.music

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.databinding.FragmentHomeMusicBinding
import com.example.spotifyclone.model.deezer.DeezerTrack
import com.example.spotifyclone.network.deezer.TrackApi
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.ui.fragments.home.music.adapter.TrackAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragmentMusic : Fragment() {
    private lateinit var binding: FragmentHomeMusicBinding


    lateinit var trackApi: TrackApi

    private val homeMusicViewModel: HomeMusicViewModel by viewModels { HomeMusicFactory(trackApi) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeMusicBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        trackApi = retrofit.create(TrackApi::class.java)
        homeMusicViewModel.getTracks()

        homeMusicViewModel.tracks.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setAdapter(it.data)
                    Log.e("track", it.data.toString())
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
    }

    private fun setAdapter(data: List<DeezerTrack>) {
        val adapter = TrackAdapter()
        adapter.submitList(data)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter
    }

}