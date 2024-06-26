package com.example.spotifyclone.ui.fragments.album

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.data.network.api.AlbumApi
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.databinding.BottomSheetTrackBinding
import com.example.spotifyclone.databinding.FragmentAlbumViewBinding
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.activity.viewmodel.MusicPlayerViewModel
import com.example.spotifyclone.ui.adapters.SingleAlbumTracksAdapter
import com.example.spotifyclone.ui.fragments.album.viewmodel.AlbumFactory
import com.example.spotifyclone.ui.fragments.album.viewmodel.AlbumViewModel
import com.example.spotifyclone.util.GsonHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumViewFragment : Fragment() {
    private lateinit var binding: FragmentAlbumViewBinding
    private lateinit var album: com.example.spotifyclone.domain.model.dto.Album
    private var mediaPlayer: MediaPlayer? = null
    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()

    @Inject
    lateinit var albumApi: AlbumApi
    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val isPlaying = MutableLiveData(false)
    private lateinit var acitivity: MainActivity
    private val albumViewModel: AlbumViewModel by viewModels {
        AlbumFactory(
            albumApi,
            firebaseAuth,
            firestore
        )
    }
    private var tracks: List<com.example.spotifyclone.domain.model.dto.MusicItem> = emptyList()
    private var imgAlbum = ""
    private lateinit var adapter: SingleAlbumTracksAdapter
    private lateinit var sharedPreference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumViewBinding.inflate(inflater)
        acitivity = requireActivity() as MainActivity
        mediaPlayer = acitivity.getMediaPlayer()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = SharedPreference(requireContext())
        setNavigation()
        getAlbumId()

        albumViewModel.insertionLiked.observe(viewLifecycleOwner) {
            if (it == -1L) {
                Toast.makeText(requireContext(), "Something wrong!", Toast.LENGTH_SHORT).show()

            } else if (it == 0L) {
                Toast.makeText(requireContext(), "Removed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show()
            }
        }
        setLayoutButton()
        getAlbum()

        if (this.tracks == musicPlayerViewModel.tracks.value || this.tracks.equals(musicPlayerViewModel.tracks.value) && mediaPlayer?.isPlaying==true) {
            isPlaying.postValue(true)
        }
        else{
            isPlaying.postValue(false)

        }
        isPlaying.observe(viewLifecycleOwner){
            if(it){
                binding.imgPlay.setImageResource(R.drawable.icon_album_pause)
            }else{
                binding.imgPlay.setImageResource(R.drawable.icon_play)
            }
        }
    }


    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getAlbumId() {
        arguments?.let {
            album = it.getSerializable("album") as com.example.spotifyclone.domain.model.dto.Album
        }
    }

    private fun getAlbum() {
        if (album.isFirebase) {

            if(album.isDeezer){
               saveAlbumDb(true,true)
                albumViewModel.checkInDB(album.id, true,true)
            }else{
                saveAlbumDb(true)
                albumViewModel.checkInDB(album.id, true)
            }

            binding.txtAlbumName.text = album.name
            binding.txtArtistName.text = album.tracks[0].artist
            imgAlbum = album.coverImg

            Glide.with(binding.root)
                .load(album.coverImg)
                .into(binding.imgAlbum)
            Glide.with(binding.root)
                .load(album.coverImg)
                .into(binding.imgArtist)
            val music = album.tracks.map {
                com.example.spotifyclone.domain.model.dto.MusicItem(
                    it.artist!!,
                    it.id!!,
                    it.name!!,
                    it.trackUri!!
                )
            }

            setAdapter(album.coverImg, music)
        } else {
            saveAlbumDb(false)
            albumViewModel.getAlbum(album.id)
            albumViewModel.checkInDB(album.id, false)
            albumViewModel.album.observe(viewLifecycleOwner) {
                val artistNames = it.artists.joinToString { artist: com.example.spotifyclone.domain.model.album.singlealbum.Artist -> artist.name + " " }
                binding.txtArtistName.text = artistNames
                binding.txtAlbumYear.text = it.release_date
                binding.txtAlbumName.text = it.name
                imgAlbum = it.images[0].url
                Glide.with(binding.root)
                    .load(it.images[0].url)
                    .into(binding.imgAlbum)
                Glide.with(binding.root)
                    .load(it.images[1].url)
                    .into(binding.imgArtist)
                val music = it.tracks.items.map {
                    com.example.spotifyclone.domain.model.dto.MusicItem(
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
        tracks: List<com.example.spotifyclone.domain.model.dto.MusicItem>
    ) {



        val model = tracks.map {
            com.example.spotifyclone.domain.model.dto.MusicItem(
                it.artist,
                it.id,
                it.name,
                it.trackUri,
                img
            )
        }


        adapter = SingleAlbumTracksAdapter(img,
            { position -> setMusicTrack(position) },
            { key, value -> saveSharedPreference(key, value) },
            { value -> saveSharedPreference(value) },
            { value -> isInSP(value) },
            { musicItem, trackId -> setBottomSheet(musicItem, trackId) },
            { removeSp() })

        adapter.submitList(model)
        this.tracks = adapter.getTracks()



        playAll()

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter

    }

    private fun setMusicTrack(position: Int) {

        GsonHelper.serializeTracks(requireContext().applicationContext, tracks)
        musicPlayerViewModel.setTracks(tracks)
        musicPlayerViewModel.setSelectedTrackPosition(position)

    }

    private fun removeSp() {
        sharedPreference.removeCurrent()
    }

    private fun setBottomSheet(musicItem: com.example.spotifyclone.domain.model.dto.MusicItem, trackId: String) {
        val dialog = BottomSheetDialog(requireContext())
        val view = BottomSheetTrackBinding.inflate(layoutInflater)

        dialog.setCancelable(true)
        dialog.setContentView(view.root)
        Glide.with(binding.root)
            .load(musicItem.img)
            .into(view.imgAlbum)
        view.txtArtistName.text = musicItem.artist
        view.txtTrackName.text = musicItem.name
        view.viewAddLiked.setOnClickListener {
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(300)
                .alpha(0.6f)
                .withEndAction {
                    it.scaleX = 1.0f
                    it.scaleY = 1.0f
                    it.alpha = 1.0f
                }.start()
            albumViewModel.insertLikedSongs(
                musicItem.name,
                musicItem.artist,
                musicItem.img,
                musicItem.trackUri
            )

        }
        albumViewModel.isInLiked.observe(viewLifecycleOwner) {
            if (it) {
                view.txtLiked.setText(R.string.bottom_sheet_txt_remove)
            } else {
                view.txtLiked.setText(R.string.bottom_sheet_txt_liked)
            }
        }

        albumViewModel.checkLikedSongs(musicItem.name)
        view.viewAddPlaylist.setOnClickListener {
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(300)
                .alpha(0.6f)
                .withEndAction {
                    it.scaleX = 1.0f
                    it.scaleY = 1.0f
                    it.alpha = 1.0f
                }.start()
            val bundle = Bundle()
            bundle.putSerializable("track", musicItem)
            findNavController().navigate(
                R.id.action_albumViewFragment_to_addPlaylistFragment,
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

    private fun saveAlbumDb(isFirebase: Boolean,isDeezer:Boolean=false) {
        binding.imgLike.setOnClickListener {
            albumViewModel.saveDB(album.id, isFirebase,isDeezer)
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

    private fun playAll() {
        binding.imgPlay.setOnClickListener {
            isPlaying.postValue(true)
            GsonHelper.serializeTracks(requireContext().applicationContext, tracks)
            musicPlayerViewModel.setTracks(tracks)
            musicPlayerViewModel.setPosition(0)
            acitivity.playAll()
        }
    }

}