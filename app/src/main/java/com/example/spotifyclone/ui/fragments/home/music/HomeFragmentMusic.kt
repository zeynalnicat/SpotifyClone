package com.example.spotifyclone.ui.fragments.home.music

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.databinding.FragmentHomeMusicBinding
import com.example.spotifyclone.domain.model.deezer.DeezerTrack
import com.example.spotifyclone.domain.model.dto.MusicItem
import com.example.spotifyclone.data.network.api.deezer.TrackApi
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.activity.MusicPlayerViewModel
import com.example.spotifyclone.ui.fragments.home.music.adapter.TrackAdapter
import com.example.spotifyclone.util.GsonHelper
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragmentMusic : Fragment() {
    private lateinit var binding: FragmentHomeMusicBinding


    @Inject
    lateinit var trackApi: TrackApi
    private lateinit var sharedPreference: SharedPreference

    private lateinit var acitivity: MainActivity

    private var tracks: List<com.example.spotifyclone.domain.model.dto.MusicItem> = emptyList()

    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()


    private val homeMusicViewModel: HomeMusicViewModel by viewModels { HomeMusicFactory(trackApi) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeMusicBinding.inflate(inflater)
        acitivity = requireActivity() as MainActivity
        sharedPreference = SharedPreference(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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


    private fun saveSharedPreference(key: String, value: String) {
        sharedPreference.saveValue(key, value)
    }

    private fun saveSharedPreference(value: Boolean) {
        sharedPreference.saveIsPlaying(value)
    }

    private fun setMusicTrack(position: Int) {
        sharedPreference.saveValue("Position", position)
        GsonHelper.serializeTracks(requireContext().applicationContext, tracks)
        musicPlayerViewModel.setSelectedTrackPosition(position)
        musicPlayerViewModel.setTracks(tracks)

    }

    private fun getIsPlaying():Boolean{

        return sharedPreference.getValue("isPlaying",false)
    }

    private fun isInSP(value: String): Boolean {
        return sharedPreference.containsValue(value)
    }



    private fun setAdapter(data: List<com.example.spotifyclone.domain.model.deezer.DeezerTrack>) {
        val adapter = TrackAdapter(isInSP = { isInSP(it) },
            setMusicLayout = { setMusicTrack(it) },
            saveSharedPreference = { key, value -> saveSharedPreference(key, value) },
            saveSharedPreferenceBool = { value -> saveSharedPreference(value) },
            isPlaying = {getIsPlaying()})
        adapter.submitList(data)
        tracks = data.map {
            com.example.spotifyclone.domain.model.dto.MusicItem(
                it.artist.name,
                "",
                it.title,
                it.preview,
                it.album.cover_xl
            )
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter
    }

}