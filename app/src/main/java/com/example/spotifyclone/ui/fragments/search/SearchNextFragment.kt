package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.BottomSheetTrackBinding
import com.example.spotifyclone.databinding.FragmentSearchNextBinding
import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.model.dto.MusicItem
import com.example.spotifyclone.model.dto.SearchModel
import com.example.spotifyclone.network.deezer.SearchApi
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.activity.MusicPlayerViewModel
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.example.spotifyclone.util.GsonHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchNextFragment : Fragment() {
    private lateinit var binding: FragmentSearchNextBinding

    private lateinit var searchApi: SearchApi

    private val searchNextViewModel: SearchNextViewModel by viewModels { SearchNextFactory(searchApi) }

    private lateinit var sharedPreference: SharedPreference

    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()

    private var tracks: List<MusicItem> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNextBinding.inflate(inflater)
        sharedPreference = SharedPreference(requireContext())
        setNavigation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        searchApi = retrofit.create(SearchApi::class.java)
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


    private fun setAdapter(list: List<SearchModel>) {
        val model = list.map { LikedSongs(it.title, it.artist, it.img, it.uri) }
        val adapter = LikedSongsAdapter({ setBottomSheet(it) },
            { setMusicTrack(it) },
            { key, value -> saveSharedPreference(key, value) },
            { value -> saveSharedPreference(value) },
            { isInSP(it) })
        adapter.submitList(model)

        tracks = list.map {
            MusicItem(
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

    private fun setBottomSheet(musicItem: LikedSongs) {
        val dialog = BottomSheetDialog(requireContext())
        val view = BottomSheetTrackBinding.inflate(layoutInflater)

        dialog.setCancelable(true)
        dialog.setContentView(view.root)

        Glide.with(binding.root).load(musicItem.imgUri).into(view.imgAlbum)

        view.txtArtistName.text = musicItem.artist
        view.txtTrackName.text = musicItem.name
        view.txtPlaylist.text = getText(R.string.playlist_txt_remove)

        view.viewAddLiked.setOnClickListener {
//            singleViewModel.insertLikedSongs(
//                musicItem.name, musicItem.artist, musicItem.imgUri, musicItem.uri
//            )

        }
//
//        singleViewModel.checkLikedSongs(musicItem.name)
//
//        singleViewModel.isInLiked.observe(viewLifecycleOwner) {
//            if (it) {
//                view.txtLiked.setText(R.string.bottom_sheet_txt_remove)
//            } else {
//                view.txtLiked.setText(R.string.bottom_sheet_txt_liked)
//            }


//            view.viewAddPlaylist.setOnClickListener {
//                singleViewModel.removeFromPlaylist(id, musicItem.name)
//                singleViewModel.getTracks(id)
//                dialog.hide()
//            }
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

