package com.example.spotifyclone.ui.fragments.single_genre

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSingleCategoryBinding
import com.example.spotifyclone.model.deezer.DeezerTrack
import com.example.spotifyclone.model.dto.Category
import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.network.retrofit.api.deezer.TrackApi
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.activity.MusicPlayerViewModel
import com.example.spotifyclone.ui.adapters.LibraryAlbumAdapter
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.example.spotifyclone.util.GsonHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SingleCategoryFragment : Fragment() {

    private lateinit var binding: FragmentSingleCategoryBinding

    private var category: Category? = null

    @Inject
    lateinit var trackApi: TrackApi

    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()

    private lateinit var acitivity: MainActivity

    private var tracks: List<MusicItem> = emptyList()

    private lateinit var sharedPreference: SharedPreference

    private val singleCategoryViewModel: SingleCategoryViewModel by viewModels {
        SingleCategoryFactory(
            trackApi
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleCategoryBinding.inflate(inflater)
        acitivity = requireActivity() as MainActivity
        sharedPreference = SharedPreference(requireContext())
        arguments?.let {
            category = it.getSerializable("category") as Category
        }

        setNavigation()

        val color = Color.parseColor(category?.color)
        binding.view.setBackgroundColor(color)

        binding.txtName.text = category?.name
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleCategoryViewModel.tracks.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setAdapter(it.data)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {}

            }
        }

        singleCategoryViewModel.getTracks(category?.tracks ?: emptyList())
    }

    private fun setAdapter(data: List<DeezerTrack>) {
        val adapter = LikedSongsAdapter(
            isInSP = {isInSP(it)},
            setMusicLayout = {setMusicTrack(it)},
           saveSharedPreference =  { key, value -> saveSharedPreference(key, value) },
           saveSharedPreferenceBool =  { value -> saveSharedPreference(value) },
        )
        val model = data.map {
            LikedSongs(
                it.title,
                it.artist.name,
                it.album.cover_medium,
                it.preview,
                isTopTracks = true
            )
        }
        adapter.submitList(model)
        tracks = model.map { MusicItem(it.artist,"",it.name,it.uri,it.imgUri) }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }


    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
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

    private fun isInSP(value: String): Boolean {
        return sharedPreference.containsValue(value)
    }


}