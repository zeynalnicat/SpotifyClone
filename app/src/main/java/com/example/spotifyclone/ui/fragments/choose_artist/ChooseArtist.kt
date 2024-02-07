package com.example.spotifyclone.ui.fragments.choose_artist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.ChooseArtistAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.example.spotifyclone.databinding.FragmentChooseArtistBinding
import com.example.spotifyclone.db.artist.ArtistsEntity
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.artist.Artist
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChooseArtist : Fragment() {
    private lateinit var binding: FragmentChooseArtistBinding
    private lateinit var adapter: ChooseArtistAdapter
    private lateinit var chooseArtistViewModel: ChoseArtistViewModel
    private var selectedArtists = mutableListOf<Artist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseArtistBinding.inflate(inflater, container, false)
        chooseArtistViewModel = ViewModelProvider(this)[ChoseArtistViewModel::class.java]
        chooseArtistViewModel.getArtists()


        search()
        setAdapter()
        setNavigation()

        return binding.root
    }


    fun updateButtonVisibility(count: Int) {
        binding.btnNext.visibility = if (count >= 3) View.VISIBLE else View.GONE
    }

    private fun setNavigation() {

        binding.btnNext.setOnClickListener {
            selectedArtists = adapter.getSelectedArtists()
            val roomDB = RoomDB.accessDb(requireContext())
            lifecycleScope.launch(Dispatchers.IO) {
                selectedArtists.forEach {
                    roomDB?.artistDao()?.insert(ArtistsEntity(0, it.name, it.id))
                }
                withContext(Dispatchers.Main) {
                    findNavController().navigate(R.id.action_chooseArtist_to_homeFragment)
                }
            }


        }

        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(
                requireActivity(),
                R.id.fragmentContainerView
            )
            navController.popBackStack(R.id.signUp43, false)
        }
    }

    private fun setAdapter() {
        chooseArtistViewModel.artists.observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()){
                adapter = ChooseArtistAdapter(this@ChooseArtist)
                val layoutManager = FlexboxLayoutManager(requireContext())
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.SPACE_BETWEEN
                layoutManager.flexWrap = FlexWrap.WRAP
                layoutManager.alignItems = AlignItems.FLEX_START
                binding.artistRecycle.layoutManager = layoutManager
                adapter.submitList(it)
                binding.artistRecycle.adapter = adapter

            }
        }


    }


    private fun search() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString()
                chooseArtistViewModel.search(text)
            }
        })

    }

}