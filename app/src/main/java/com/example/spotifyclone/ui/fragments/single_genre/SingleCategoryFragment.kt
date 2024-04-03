package com.example.spotifyclone.ui.fragments.single_genre

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.data.network.api.deezer.TrackApi
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.databinding.FragmentSingleCategoryBinding
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.activity.viewmodel.MusicPlayerViewModel
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.example.spotifyclone.ui.fragments.single_genre.viewmodel.SingleCategoryFactory
import com.example.spotifyclone.ui.fragments.single_genre.viewmodel.SingleCategoryViewModel
import com.example.spotifyclone.util.GsonHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SingleCategoryFragment : Fragment() {

    private lateinit var binding: FragmentSingleCategoryBinding

    private var category: com.example.spotifyclone.domain.model.dto.Category? = null

    @Inject
    lateinit var trackApi: TrackApi

    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()

    private lateinit var acitivity: MainActivity

    private var tracks: List<com.example.spotifyclone.domain.model.dto.MusicItem> = emptyList()

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
            category = it.getSerializable("category") as com.example.spotifyclone.domain.model.dto.Category
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

                is Resource.Loading->{

                }
            }
        }

        singleCategoryViewModel.getTracks(category?.tracks ?: emptyList())
    }

    private fun setAdapter(data: List<com.example.spotifyclone.domain.model.deezer.DeezerTrack>) {
        val adapter = LikedSongsAdapter(
            isInSP = {isInSP(it)},
            setMusicLayout = {setMusicTrack(it)},
           saveSharedPreference =  { key, value -> saveSharedPreference(key, value) },
           saveSharedPreferenceBool =  { value -> saveSharedPreference(value) },
        )
        val model = data.map {
            com.example.spotifyclone.domain.model.dto.LikedSongs(
                name = it.title,
                artist = it.artist.name,
                imgUri = it.album.cover_xl,
                uri = it.preview,
                isFromGenre = true
            )
        }
        adapter.submitList(model)
        tracks = model.map {
            com.example.spotifyclone.domain.model.dto.MusicItem(
                artist = it.artist,
                id = "",
                name = it.name,
                trackUri = it.uri,
                img = it.imgUri
            )
        }
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
        musicPlayerViewModel.setTracks(tracks)
        musicPlayerViewModel.setSelectedTrackPosition(position)


    }

    private fun isInSP(value: String): Boolean {
        return sharedPreference.containsValue(value)
    }


}