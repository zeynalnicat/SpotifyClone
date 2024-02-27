package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.BottomSheetTrackBinding
import com.example.spotifyclone.databinding.FragmentSearchNextBinding
import com.example.spotifyclone.model.dto.LikedSongs
import com.example.spotifyclone.model.dto.SearchModel
import com.example.spotifyclone.network.deezer.SearchApi
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchNextFragment : Fragment() {
    private lateinit var binding: FragmentSearchNextBinding

    private lateinit var searchApi: SearchApi

    private val searchNextViewModel: SearchNextViewModel by viewModels { SearchNextFactory(searchApi) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNextBinding.inflate(inflater)
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
                    setAdapter(it.data)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {}


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
        val adapter = LikedSongsAdapter { setBottomSheet(it) }
        adapter.submitList(model)
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

}

