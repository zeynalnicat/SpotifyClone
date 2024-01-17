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
import com.example.spotifyclone.databinding.FragmentAlbumViewBinding
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.model.album.singlealbum.Artist
import com.example.spotifyclone.model.pseudo_models.MusicItem
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.viewmodels.AlbumViewModel

class AlbumViewFragment : Fragment() {
    private lateinit var binding: FragmentAlbumViewBinding
    private var albumId = ""
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var sharedPreference : SharedPreference


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
            albumViewModel.checkInDB(it, albumId)
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
            albumId = it.getString("albumId", "")
        }
    }

    private fun getAlbum() {
        albumViewModel.getAlbum(albumId)
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
            setAdapter(it.images[0].url, it.tracks.items)
        }
    }

    private fun setAdapter(
        img: String,
        tracks: List<com.example.spotifyclone.model.album.singlealbum.Item>
    ) {
        val adapter = SingleAlbumTracksAdapter(img,
            { setMusicTrack() },
            {key,value->saveSharedPreference(key,value)},{value-> saveSharedPreference(value)},{value-> isInSP(value)})
        val musicTracks = tracks.map { MusicItem(it, isPlayed = false)}
        adapter.submitList(musicTracks)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter

    }

    private fun setMusicTrack() {
        val activity = requireActivity() as MainActivity
        activity.setMusicPlayer(true)
    }


    private fun saveSharedPreference(key:String,value:String){
        sharedPreference.saveValue(key,value)
    }

    private fun saveSharedPreference(value: Boolean){
        sharedPreference.saveIsPlaying(value)
    }

    private fun isInSP(value: String):Boolean{
        return sharedPreference.containsValue(value)
    }

    private fun saveAlbumDb(){
        val roomDB = RoomDB.accessDb(requireContext())
        binding.imgLike.setOnClickListener {
            roomDB?.let {
                albumViewModel.saveDB(it, albumId)
            }
        }

    }

    private fun setLayoutButton(){
        albumViewModel.isInDB.observe(viewLifecycleOwner){
            binding.imgLike.setImageResource(
                when(it){
                    true -> R.drawable.icon_filled_heart
                    false -> R.drawable.icon_like
                }
            )
        }
    }

}