package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.data.network.api.deezer.SearchApi
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.databinding.BottomSheetTrackBinding
import com.example.spotifyclone.databinding.FragmentSearchNextBinding
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.activity.viewmodel.MusicPlayerViewModel
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.example.spotifyclone.ui.fragments.search.viewmodel.SearchNextFactory
import com.example.spotifyclone.ui.fragments.search.viewmodel.SearchNextViewModel
import com.example.spotifyclone.util.GsonHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SearchNextFragment : Fragment() {
    private lateinit var binding: FragmentSearchNextBinding

    @Inject
    lateinit var searchApi: SearchApi

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val searchNextViewModel: SearchNextViewModel by viewModels {
        SearchNextFactory(
            searchApi,
            firestore,
            firebaseAuth
        )
    }

    private lateinit var sharedPreference: SharedPreference


    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()

    private var tracks: List<com.example.spotifyclone.domain.model.dto.MusicItem> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNextBinding.inflate(inflater)
        sharedPreference = SharedPreference(requireContext())
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigation(false)
        setNavigation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search()

        searchNextViewModel.searchResults.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.txtAbout.visibility = View.GONE
                    binding.txtDetails.visibility = View.GONE
                    setAdapter(it.data)
                }

                is Resource.Error -> {
                    binding.txtAbout.visibility = View.GONE
                    binding.txtDetails.visibility = View.GONE
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {
                    binding.txtAbout.visibility = View.VISIBLE
                    binding.txtDetails.visibility = View.VISIBLE
                }


            }
        }
        binding.edtSearch.requestFocus()
        binding.edtSearch.setOnFocusChangeListener { view, b ->
            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
        val inputMethodManager =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT)


    }

    private fun setNavigation() {
        binding.txtCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun search() {


        binding.edtSearch.doAfterTextChanged {
            searchNextViewModel.search(it.toString())
        }
    }


    private fun setAdapter(list: List<com.example.spotifyclone.domain.model.dto.SearchModel>) {
        val model = list.map {
            com.example.spotifyclone.domain.model.dto.LikedSongs(
                it.title,
                it.artist,
                it.img,
                it.uri
            )
        }
        val adapter = LikedSongsAdapter({ setBottomSheet(it) },
            { setMusicTrack(it) },
            { key, value -> saveSharedPreference(key, value) },
            { value -> saveSharedPreference(value) },
            { isInSP(it) })
        adapter.submitList(model)

        tracks = list.map {
            com.example.spotifyclone.domain.model.dto.MusicItem(
                artist = it.artist,
                name = it.title,
                img = it.img,
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

        Glide.with(binding.root).load(musicItem.imgUri).into(view.imgAlbum)

        view.txtArtistName.text = musicItem.artist
        view.txtTrackName.text = musicItem.name

        view.viewAddLiked.setOnClickListener {
            searchNextViewModel.insertLikedSongs(
                musicItem.name, musicItem.artist, musicItem.imgUri, musicItem.uri
            )

            dialog.hide()

        }

        searchNextViewModel.checkLikedSongs(musicItem.name)

        searchNextViewModel.isInLiked.observe(viewLifecycleOwner) {
            if (it) {
                view.txtLiked.setText(R.string.bottom_sheet_txt_remove)
            } else {
                view.txtLiked.setText(R.string.bottom_sheet_txt_liked)
            }


            view.viewAddPlaylist.setOnClickListener {
                val bundle = Bundle()
                val musicModel =
                    com.example.spotifyclone.domain.model.dto.MusicItem(
                        musicItem.artist,
                        "",
                        musicItem.name,
                        musicItem.uri,
                        musicItem.imgUri
                    )
                bundle.putSerializable("track", musicModel)
                findNavController().navigate(
                    R.id.action_searchNextFragment_to_addPlaylistFragment,
                    bundle
                )
                dialog.hide()

            }
            dialog.show()

        }
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

