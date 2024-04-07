package com.example.spotifyclone.ui.fragments.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.databinding.BottomSheetTrackBinding
import com.example.spotifyclone.databinding.FragmentUserLibraryBinding
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.adapters.PlaylistAdapter
import com.example.spotifyclone.ui.fragments.library.viewmodel.UserLibraryFactory
import com.example.spotifyclone.ui.fragments.library.viewmodel.UserLibraryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UserLibraryFragment : Fragment() {
    private lateinit var binding: FragmentUserLibraryBinding

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val userLibraryViewModel: UserLibraryViewModel by viewModels {
        UserLibraryFactory(
            firebaseAuth,
            firestore
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserLibraryBinding.inflate(inflater)
        setNavigation()
        val activity = requireActivity() as MainActivity
        val sp = SharedPreference(requireContext())

        if(sp.getValue("isPlaying",false)){
            activity.setMusicPlayer(true)
        }else{
            activity.setMusicPlayer(false)
        }
        setLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userLibraryViewModel.getPlaylists()
        userLibraryViewModel.playlists.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visibility=View.GONE
                    setAdapter(it.data)
                }

                is Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                }

                else -> {
                    binding.progressBar.visibility=View.VISIBLE
                }

            }

        }

    }

    private fun setLayout() {
        val userRef = firestore.collection("users")
        val query = userRef.whereEqualTo("userId", firebaseAuth.currentUser?.uid)
        try {
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val gender = querySnapshot.documents[0].getString("gender")
                        val name = querySnapshot.documents[0].getString("username")
                        binding.txtName.text = name ?: "N/A"
                        binding.txtEmail.text = firebaseAuth.currentUser?.email
                        val image = querySnapshot.documents[0].getString("img")
                        if (image?.isEmpty() == true) {
                            binding.imgProfile.setImageResource(if (gender == "Men" || gender==null) R.drawable.man_icon else R.drawable.woman_icon)
                        } else {
                            Glide.with(binding.root)
                                .load(image.toString())
                                .into(binding.imgProfile)
                        }

                    } else {
                        binding.txtName.text = "N/A"
                    }
                }
                .addOnFailureListener { exception ->
                    binding.txtName.text = "N/A"
                    Log.e("Firestore", "Error retrieving user data: ${exception.message}")
                }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_userLibraryFragment_to_editProfileFragment)
        }
    }

    private fun setAdapter(list: List<com.example.spotifyclone.domain.model.dto.PlaylistModel>) {
        val adapter =
            PlaylistAdapter ({ bundle ->
                findNavController().navigate(
                    R.id.action_userLibraryFragment_to_singlePlaylistFragment,
                    bundle
                )
            },{setBottomSheet(it)})
        adapter.submitList(list)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter

    }


    private fun setBottomSheet(playlistModel: com.example.spotifyclone.domain.model.dto.PlaylistModel) {
        val dialog = BottomSheetDialog(requireContext())
        val view = BottomSheetTrackBinding.inflate(layoutInflater)

        dialog.setCancelable(true)
        dialog.setContentView(view.root)
        view.txtLiked.visibility = View.GONE
        view.iconLibrary.visibility= View.GONE
        view.txtPlaylist.setText(R.string.txt_remove_playlist)
        view.txtArtistName.text = playlistModel.countTrack.toString() + " " +  getString(R.string.library_txt_songs)
        view.txtTrackName.text = playlistModel.name
        view.viewAddLiked.visibility = View.GONE
        view.imgAlbum.setImageResource(R.drawable.playlist_image)
        view.imageView3.setImageResource(R.drawable.icon_delete)


        view.viewAddPlaylist.setOnClickListener {
           userLibraryViewModel.removeFromPlaylist(playlistModel.id)
           dialog.hide()
        }
        dialog.show()

    }




}