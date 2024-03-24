package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.BottomSheetTrackBinding
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.example.spotifyclone.databinding.FragmentLikedSongsBinding

import com.example.spotifyclone.domain.model.dto.LikedSongs
import com.example.spotifyclone.domain.model.dto.MusicItem
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.ui.activity.MusicPlayerViewModel
import com.example.spotifyclone.util.GsonHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
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

    private lateinit var sharedPreference: SharedPreference

    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()

    private var tracks: List<com.example.spotifyclone.domain.model.dto.MusicItem> = emptyList()

    private val likedSongsViewModel: LikedSongsViewModel by viewModels {
        LikedSongsFactory(
            firestore,
            firebaseAuth
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikedSongsBinding.inflate(inflater)
        sharedPreference = SharedPreference(requireContext())
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
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {

                }


            }
        }

        binding.edtSearch.setOnFocusChangeListener { view, b ->
            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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

    private fun setAdapter(songs: List<com.example.spotifyclone.domain.model.dto.LikedSongs>) {
        val adapter = LikedSongsAdapter({ setBottomSheet(it) },
            { setMusicTrack(it) },
            { key, value -> saveSharedPreference(key, value) },
            { value -> saveSharedPreference(value) },
            { isInSP(it) })
        adapter.submitList(songs)
        tracks = songs.map {
            com.example.spotifyclone.domain.model.dto.MusicItem(
                artist = it.artist,
                name = it.name,
                img = it.imgUri,
                id = "",
                trackUri = it.uri
            )
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter
    }

    private fun setBottomSheet(musicItem: com.example.spotifyclone.domain.model.dto.LikedSongs) {
        val dialog = BottomSheetDialog(requireContext())
        val view = BottomSheetTrackBinding.inflate(layoutInflater)

        dialog.setCancelable(true)
        dialog.setContentView(view.root)

        Glide.with(binding.root)
            .load(musicItem.imgUri)
            .into(view.imgAlbum)

        view.txtArtistName.text = musicItem.artist
        view.txtTrackName.text = musicItem.name
        view.txtLiked.setText(getText(R.string.bottom_sheet_txt_remove))


        view.viewAddLiked.setOnClickListener {
            likedSongsViewModel.removeLikedSongs(musicItem.name)
            dialog.hide()
        }

        view.viewAddPlaylist.setOnClickListener {
            val bundle = Bundle()
            val model = com.example.spotifyclone.domain.model.dto.MusicItem(
                artist = musicItem.artist,
                id = "",
                name = musicItem.name,
                img = musicItem.imgUri,
                trackUri = musicItem.uri
            )
            bundle.putSerializable("track", model)
            findNavController().navigate(
                R.id.action_likedSongsFragment_to_addPlaylistFragment,
                bundle
            )
            dialog.hide()
        }
        dialog.show()

    }

    private fun saveSharedPreference(key: String, value: String) {
        sharedPreference.saveValue(key, value)
    }

    private fun saveSharedPreference(value: Boolean) {
        sharedPreference.saveIsPlaying(value)
    }

    private fun isInSP(value: String): Boolean {
        return sharedPreference.containsValue(value)
    }


    private fun setMusicTrack(position: Int) {
        sharedPreference.saveValue("Position", position)
        GsonHelper.serializeTracks(requireContext().applicationContext, tracks)
        musicPlayerViewModel.setSelectedTrackPosition(position)
        musicPlayerViewModel.setTracks(tracks)


    }

}