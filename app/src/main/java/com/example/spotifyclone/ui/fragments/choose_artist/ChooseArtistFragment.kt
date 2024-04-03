package com.example.spotifyclone.ui.fragments.choose_artist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.data.network.api.ArtistsApi
import com.example.spotifyclone.databinding.FragmentChooseArtistBinding
import com.example.spotifyclone.domain.model.artist.Artist
import com.example.spotifyclone.ui.adapters.ChooseArtistAdapter
import com.example.spotifyclone.ui.fragments.choose_artist.viewmodel.ChooseArtistFactory
import com.example.spotifyclone.ui.fragments.choose_artist.viewmodel.ChoseArtistViewModel
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChooseArtistFragment : Fragment() {
    private lateinit var binding: FragmentChooseArtistBinding
    private lateinit var adapter: ChooseArtistAdapter

    @Inject
    lateinit var artistsApi: ArtistsApi

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    private val chooseArtistViewModel: ChoseArtistViewModel by viewModels {
        ChooseArtistFactory(
            artistsApi
        )
    }
    private var selectedArtists = mutableListOf<Artist>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseArtistBinding.inflate(inflater, container, false)
        search()
        setAdapter()
        setNavigation()
        return binding.root
    }


    fun updateButtonVisibility(count: Int) {
        binding.btnNext.visibility = if (count >= 3) View.VISIBLE else View.GONE
    }

    private fun setNavigation() {
        val artistRef = firestore.collection("favArtist")
        binding.btnNext.setOnClickListener {
            selectedArtists = adapter.getSelectedArtists()
            try {
                selectedArtists.forEach {
                    val artistModel = hashMapOf(
                        "artistId" to it.id,
                        "artistName" to it.name,
                        "userId" to firebaseAuth.currentUser?.uid
                    )
                    artistRef.add(artistModel)
                }
                findNavController().navigate(R.id.action_chooseArtist_to_homeFragment)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(
                requireActivity(),
                R.id.fragmentContainerView
            )
            navController.popBackStack(R.id.signUp43, false)
        }

        chooseArtistViewModel.getArtists()

    }

    private fun setAdapter() {
        chooseArtistViewModel.artists.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                adapter = ChooseArtistAdapter(this@ChooseArtistFragment)
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