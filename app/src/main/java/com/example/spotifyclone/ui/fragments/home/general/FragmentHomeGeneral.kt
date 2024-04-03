package com.example.spotifyclone.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.data.network.TokenRefresher
import com.example.spotifyclone.data.network.api.AlbumApi
import com.example.spotifyclone.data.network.api.ArtistsApi
import com.example.spotifyclone.data.network.api.deezer.TrackApi
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.databinding.FragmentHomeGeneralBinding
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.activity.viewmodel.MusicPlayerViewModel
import com.example.spotifyclone.ui.adapters.AlbumAdapter
import com.example.spotifyclone.ui.adapters.ArtistAdapter
import com.example.spotifyclone.ui.adapters.LikedSongsAdapter
import com.example.spotifyclone.ui.fragments.home.viewmodel.HomeFactory
import com.example.spotifyclone.ui.fragments.home.viewmodel.HomeViewModel
import com.example.spotifyclone.util.GsonHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint

class FragmentHomeGeneral : Fragment() {
    private lateinit var binding: FragmentHomeGeneralBinding

    @Inject
    lateinit var artistsApi: ArtistsApi

    @Inject
    lateinit var albumApi: AlbumApi

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var tokenRefresher: TokenRefresher

    @Inject
    lateinit var trackApi: TrackApi

    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()

    private lateinit var acitivity: MainActivity

    private var tracks: List<com.example.spotifyclone.domain.model.dto.MusicItem> = emptyList()

    private lateinit var sharedPreference: SharedPreference

    @Inject
    lateinit var albumDeezerApi: com.example.spotifyclone.data.network.api.deezer.AlbumApi

    private val homeViewModel: HomeViewModel by viewModels {
        HomeFactory(
            albumApi,
            artistsApi,
            firestore,
            firebaseAuth,
            tokenRefresher,
            trackApi,
            albumDeezerApi
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeGeneralBinding.inflate(inflater)
        setBottom()
        acitivity = requireActivity() as MainActivity
        sharedPreference = SharedPreference(requireContext())
        acitivity.checkVisibility()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeDatas()

        homeViewModel.getNewRelease()
        homeViewModel.getArtist()
        homeViewModel.getPopularAlbums()
        homeViewModel.setRecommended()
        homeViewModel.getTopMusics()


    }

    private fun observeDatas() {

        homeViewModel.newReleases.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.txtNewRelease.visibility = View.VISIBLE
                    binding.recyclerViewNewRelease.visibility = View.VISIBLE
                    setNewRelease(it.data)
                }

                is Resource.Error -> {
                    binding.txtNewRelease.visibility = View.GONE
                    binding.recyclerViewNewRelease.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()

                }

                is Resource.Loading -> {}
            }

        }

        homeViewModel.artists.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setTrySomethingElse(it.data)
                    binding.recyclerTrySomething.visibility = View.VISIBLE
                    binding.txtTry.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    binding.recyclerTrySomething.visibility = View.GONE
                    binding.txtTry.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()
                }

                is Resource.Loading -> {}


            }
        }

        homeViewModel.topMusics.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setTopMusics(it.data)
                }

                is Resource.Error -> {

                }

                is Resource.Loading -> {}

            }
        }



        homeViewModel.popularAlbums.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setPopularAlbums(it.data)
                    binding.recyclerViewPopular.visibility = View.VISIBLE
                    binding.txtPopularAlbums.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    binding.recyclerViewPopular.visibility = View.GONE
                    binding.txtPopularAlbums.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()
                }

                is Resource.Loading -> {}


            }
        }

        homeViewModel.recommended.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.txtRecommend.visibility = View.VISIBLE
                    binding.recyclerViewRecommended.visibility = View.VISIBLE
                    setRecommended(it.data)
                }

                is Resource.Error -> {
                    binding.txtRecommend.visibility = View.GONE
                    binding.recyclerViewRecommended.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()
                }

                is Resource.Loading -> {}


            }
        }

        homeViewModel.someAlbums.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    deezerAlbums(it.data)
                }

                is Resource.Error -> {
                    binding.txtRecommend.visibility = View.GONE
                    binding.recyclerViewRecommended.visibility = View.GONE
//                    Toast.makeText(requireContext(), it.exception.message ?: "", Toast.LENGTH_SHORT)
//                        .show()
                }

                is Resource.Loading -> {}


            }
        }
    }

    private fun setTopMusics(data: List<com.example.spotifyclone.domain.model.dto.MusicItem>) {
        val adapter = LikedSongsAdapter(
            isInSP = { isInSP(it) },
            setMusicLayout = { setMusicTrack(it) },
            saveSharedPreference = { key, value -> saveSharedPreference(key, value) },
            saveSharedPreferenceBool = { value -> saveSharedPreference(value) },
        )
        val model =
            data.map {
                com.example.spotifyclone.domain.model.dto.LikedSongs(
                    it.name,
                    it.artist,
                    it.img,
                    it.trackUri,
                    isTopTracks = true
                )
            }
        adapter.submitList(model)
        tracks = data
        binding.recyclerTopMusics.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerTopMusics.adapter = adapter

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


    private fun setBottom() {
        val activity = requireActivity() as? MainActivity
        activity?.setBottomNavigation(true)
    }


    private fun setNewRelease(list: List<com.example.spotifyclone.domain.model.dto.Album>) {

        val adapter =
            AlbumAdapter {
                findNavController().navigate(
                    R.id.action_homeFragment_to_albumViewFragment,
                    it
                )
            }
        adapter.submitList(list)
        binding.recyclerViewNewRelease.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewNewRelease.adapter = adapter

    }


    private fun setTrySomethingElse(list: List<com.example.spotifyclone.domain.model.artist.Artist>) {

        val adapter =
            ArtistAdapter {
                findNavController().navigate(
                    R.id.action_homeFragment_to_artistViewFragment,
                    it
                )
            }
        adapter.submitList(list)
        binding.recyclerTrySomething.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerTrySomething.adapter = adapter

    }


    private fun setPopularAlbums(list: List<com.example.spotifyclone.domain.model.dto.Album>) {

        val adapter =
            AlbumAdapter {
                findNavController().navigate(
                    R.id.action_homeFragment_to_albumViewFragment,
                    it
                )
            }

        adapter.submitList(list)
        binding.recyclerViewPopular.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewPopular.adapter = adapter
    }


    private fun setRecommended(list: List<com.example.spotifyclone.domain.model.dto.Album>) {
        val adapter = AlbumAdapter {
            findNavController().navigate(
                R.id.action_homeFragment_to_albumViewFragment,
                it
            )
        }
        adapter.submitList(list)
        binding.recyclerViewRecommended.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewRecommended.adapter = adapter


    }


    private fun deezerAlbums(list: List<com.example.spotifyclone.domain.model.dto.Album>) {

    }
}


//  val constraintLayout: ConstraintLayout = binding.constraintLayout
//        var previousViewId = binding.recyclerViewRecommended.id
//
//        // Create layout params for TextView
//        val textLayoutParams = ConstraintLayout.LayoutParams(
//            0,
//            ConstraintLayout.LayoutParams.WRAP_CONTENT
//        )
//
//
//        val textView = TextView(requireContext())
//        textView.id = View.generateViewId()
//        textView.text = "For you"
//        textView.setTextColor(Color.WHITE)
//        textView.textSize = 20f
//        constraintLayout.addView(textView, textLayoutParams)
//
//        // Create layout params for RecyclerView
//        val recyclerLayoutParams = ConstraintLayout.LayoutParams(
//            0,
//            ConstraintLayout.LayoutParams.WRAP_CONTENT
//        )
//
//
//        val recyclerView = RecyclerView(requireContext()).apply {
//            id = View.generateViewId()
//            itemAnimator = null
//            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        }
//
//
//
//
//        constraintLayout.addView(recyclerView, recyclerLayoutParams)
//
//
//        val textViewConstraints = ConstraintSet()
//        textViewConstraints.clone(constraintLayout)
//
//        textViewConstraints.connect(
//            textView.id,
//            ConstraintSet.TOP,
//            previousViewId,
//            ConstraintSet.BOTTOM
//        )
//        textViewConstraints.connect(
//            textView.id,
//            ConstraintSet.START,
//            binding.recyclerViewRecommended.id,
//            ConstraintSet.START
//        )
//        textViewConstraints.connect(
//            textView.id,
//            ConstraintSet.END,
//            binding.recyclerViewRecommended.id,
//            ConstraintSet.END
//        )
//
//        val adapter = AlbumAdapter {
//            findNavController().navigate(
//                R.id.action_homeFragment_to_albumViewFragment,
//                it
//            )
//        }
//
//        adapter.submitList(list.toMutableList())
//        recyclerView.adapter = adapter
//        textViewConstraints.applyTo(constraintLayout)
//
//        textViewConstraints.clone(constraintLayout)
//
//        textViewConstraints.connect(
//            recyclerView.id,
//            ConstraintSet.TOP,
//            textView.id,
//            ConstraintSet.BOTTOM
//        )
//        textViewConstraints.connect(
//            recyclerView.id,
//            ConstraintSet.START,
//            binding.recyclerViewRecommended.id,
//            ConstraintSet.START
//        )
//        textViewConstraints.connect(
//            recyclerView.id,
//            ConstraintSet.END,
//            binding.recyclerViewRecommended.id,
//            ConstraintSet.END
//        )
//
//        textViewConstraints.applyTo(constraintLayout)
//
//        previousViewId = recyclerView.id
//
//    }

