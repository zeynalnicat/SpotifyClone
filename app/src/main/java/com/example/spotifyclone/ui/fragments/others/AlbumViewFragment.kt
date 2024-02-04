package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.util.copy
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.SingleAlbumTracksAdapter
import com.example.spotifyclone.databinding.BottomSheetTrackBinding
import com.example.spotifyclone.databinding.FragmentAlbumViewBinding
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.album.singlealbum.Artist
import com.example.spotifyclone.model.pseudo_models.Album
import com.example.spotifyclone.model.pseudo_models.MusicItem
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.viewmodels.AlbumViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class AlbumViewFragment : Fragment() {
    private lateinit var binding: FragmentAlbumViewBinding
    private lateinit var album: Album

    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var sharedPreference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumViewBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albumViewModel = ViewModelProvider(this)[AlbumViewModel::class.java]
        sharedPreference = SharedPreference(requireContext())
        val roomDB = RoomDB.accessDb(requireContext())
        setNavigation()
        getAlbumId()
        roomDB?.let {
            albumViewModel.checkInDB(it, album.id)
        }
        setLayoutButton()
        saveAlbumDb()
        getAlbum()

    }

    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun getAlbumId() {
        arguments?.let {
            album = it.getSerializable("album") as Album
        }
    }

    private fun getAlbum() {
        if (album.isFirebase) {
            binding.txtAlbumName.text = album.name
            binding.txtArtistName.text = album.tracks[0].artist
            Glide.with(binding.root)
                .load(album.coverImg)
                .into(binding.imgAlbum)
            Glide.with(binding.root)
                .load(album.coverImg)
                .into(binding.imgArtist)
            val music = album.tracks.map {
                MusicItem(it.artist!!, it.id!!, it.name!!, it.trackUri!!)
            }
            setAdapter(album.coverImg, music)
        } else {
            albumViewModel.getAlbum(album.id)
            albumViewModel.album.observe(viewLifecycleOwner) {
                val artistNames = it.artists.joinToString { artist: Artist -> artist.name + " " }
                binding.txtArtistName.text = artistNames
                binding.txtAlbumYear.text = it.release_date
                binding.txtAlbumName.text = it.name
                Glide.with(binding.root)
                    .load(it.images[0].url)
                    .into(binding.imgAlbum)
                Glide.with(binding.root)
                    .load(it.images[1].url)
                    .into(binding.imgArtist)
                val music = it.tracks.items.map {
                    MusicItem(
                        it.artists[0].name,
                        it.id,
                        it.name,
                        "https://firebasestorage.googleapis.com/v0/b/spotify-42372.appspot.com/o/tracks%2FAlan%20Walker%20-%20Intro.mp3?alt=media&token=34d259fc-8828-4587-8182-37bf4c994ea5"
                    )
                }
                setAdapter(it.images[0].url, music)
            }
        }
    }

    private fun setAdapter(
        img: String,
        tracks: List<MusicItem>
    ) {
        val adapter = SingleAlbumTracksAdapter(img,
            { setMusicTrack() },
            { key, value -> saveSharedPreference(key, value) },
            { value -> saveSharedPreference(value) },
            { value -> isInSP(value) },
            { img, track, artist -> setBottomSheet(img, track, artist) })

        adapter.submitList(tracks)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter

    }

    private fun setMusicTrack() {
        val activity = requireActivity() as MainActivity
        activity.setMusicPlayer(true)
    }

    private fun setBottomSheet(img: String, track: String, artist: String) {
        val dialog = BottomSheetDialog(requireContext())
        val view = BottomSheetTrackBinding.inflate(layoutInflater)

        dialog.setCancelable(true)
        dialog.setContentView(view.root)

        Glide.with(binding.root)
            .load(img)
            .into(view.imgAlbum)

        view.txtArtistName.text = artist
        view.txtTrackName.text = track

        view.viewAddPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_albumViewFragment_to_addPlaylistFragment)
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

    private fun saveAlbumDb() {
        val roomDB = RoomDB.accessDb(requireContext())
        binding.imgLike.setOnClickListener {
            roomDB?.let {
                albumViewModel.saveDB(it, album.id)
            }
        }

    }

    private fun setLayoutButton() {
        albumViewModel.isInDB.observe(viewLifecycleOwner) {
            binding.imgLike.setImageResource(
                when (it) {
                    true -> R.drawable.icon_filled_heart
                    false -> R.drawable.icon_like
                }
            )
        }
    }

}