package com.example.spotifyclone.ui.fragments.track

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.databinding.FragmentTrackViewBinding
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.activity.viewmodel.MusicPlayerViewModel
import com.example.spotifyclone.ui.fragments.track.viewmodel.TrackFactory
import com.example.spotifyclone.ui.fragments.track.viewmodel.TrackViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class TrackViewFragment : Fragment() {
    private lateinit var binding: FragmentTrackViewBinding

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels()


    private lateinit var sharedPreference: SharedPreference
    private val trackViewModel: TrackViewModel by viewModels {
        TrackFactory(
            firebaseAuth,
            firestore
        )
    }
    private var musicImg = ""

    private lateinit var activity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackViewBinding.inflate(inflater)
        activity = requireActivity() as MainActivity
        activity.setBottomNavigation(false)
        setNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = SharedPreference(requireContext())

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity.setBottomNavigation(true)
                activity.setMusicPlayer(true)
                activity.supportFragmentManager.popBackStack()
            }


        }



        trackViewModel.isInLiked.observe(viewLifecycleOwner) {
            if (it) {
                binding.iconLike.setImageResource(R.drawable.icon_filled_heart)
            } else {
                binding.iconLike.setImageResource(R.drawable.icon_like)
            }
        }

        activity.getCurrentTrack()


        musicPlayerViewModel.currentTrack.observe(viewLifecycleOwner) {
            Log.d("TrackViewFragment", "Current track changed: $it.name")
            musicImg = sharedPreference.getValue("PlayingMusicImg", "")
            binding.txtTrackName.text = it.name
            binding.txtTrackHeader.text = it.name
            binding.txtArtistName.text = it.artist
            Glide.with(binding.root)
                .load(musicImg)
                .into(binding.imgTrack)
            trackViewModel.checkLikedSongs(it.name)

            binding.iconLike.setOnClickListener { view ->
                trackViewModel.insertLikedSongs(it.name, it.artist, musicImg, it.trackUri)
            }

            setMusic()

            binding.imgNext.setOnClickListener {
                it.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(300)
                    .alpha(0.6f)
                    .withEndAction {
                        it.scaleX = 1.0f
                        it.scaleY = 1.0f
                        it.alpha = 1.0f
                    }
                    .start()

//                val intent = Intent(MusicPlayerService.BROADCAST_ACTION)
//                intent.putExtra(
//                    MusicPlayerService.EXTRA_ACTION_TYPE,
//                    MusicPlayerService.ACTION_NEXT
//                )
//                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                activity.nextSong()
                activity.getCurrentTrack()
                binding.seekBar.progress = 0

            }

            binding.imgPrevious.setOnClickListener {
                it.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(300)
                    .alpha(0.6f)
                    .withEndAction {
                        it.scaleX = 1.0f
                        it.scaleY = 1.0f
                        it.alpha = 1.0f
                    }
                    .start()
                activity.prevSong()
                activity.getCurrentTrack()

                binding.seekBar.progress = 0

            }

        }


        trackViewModel.getCurrentTrack(sharedPreference)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)

    }

    private fun setNavigation() {
        binding.imgShrink.setOnClickListener {
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(300)
                .alpha(0.6f)
                .withEndAction {
                    it.scaleX = 1.0f
                    it.scaleY = 1.0f
                    it.alpha = 1.0f
                }
                .start()
            activity.setMusicPlayer(true)
            activity.setBottomNavigation(true)
            activity.supportFragmentManager.popBackStack()
        }
    }


    private fun setMusic() {

        val music = activity.getMediaPlayer()

        music?.let { media ->
            var totalTime = 0

            binding.imgPause.setOnClickListener { view ->
                view.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(300)
                    .alpha(0.6f)
                    .withEndAction {
                        view.scaleX = 1.0f
                        view.scaleY = 1.0f
                        view.alpha = 1.0f
                    }
                    .start()
                if (media.isPlaying) {
                    media.pause()
                    binding.imgPause.setImageResource(R.drawable.icon_music_view_resume)

                } else if (!media.isPlaying) {
                    media.start()
                    binding.imgPause.setImageResource(R.drawable.icon_music_view_pause)
                }
            }

            binding.seekBar.postDelayed(object : Runnable {
                override fun run() {
                    totalTime = media.duration
                    binding.txtTimeStart.text = formatDuration(media.currentPosition)
                    if (media.isPlaying) {
                        binding.imgPause.setImageResource(R.drawable.icon_music_view_pause)

                    } else if (!media.isPlaying) {
                        binding.imgPause.setImageResource(R.drawable.icon_music_view_resume)
                    }

                    binding.txtTimeEnd.text = formatDuration(totalTime)
                    binding.seekBar.progress =
                        (media.currentPosition.toFloat() / totalTime * 100).toInt()
                    binding.seekBar.postDelayed(this, 1000)

                }
            }, 0)

            binding.seekBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekbar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) {
                            val position = (progress.toFloat() / seekbar!!.max) * totalTime
                            media.seekTo(position.toInt())
                        }
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {}
                    override fun onStopTrackingTouch(p0: SeekBar?) {}
                }
            )


        }
    }

    private fun formatDuration(durationInSeconds: Int): String {
        var time = durationInSeconds / 1000
        val minutes = time / 60
        val seconds = time % 60
        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        return formattedTime
    }


}