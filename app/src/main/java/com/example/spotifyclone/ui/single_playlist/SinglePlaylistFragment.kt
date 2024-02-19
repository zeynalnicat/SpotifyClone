package com.example.spotifyclone.ui.single_playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSinglePlaylistBinding
import com.example.spotifyclone.resource.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.sin


@AndroidEntryPoint

class SinglePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentSinglePlaylistBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var id:String

    private val singleViewModel: SinglePlaylistViewModel by viewModels {
        SinglePlaylistFactory(
            firestore,
            firebaseAuth
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSinglePlaylistBinding.inflate(inflater)
        setNavigation()
        arguments?.let {
            id = it.getString("id","")
        }
        setLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        singleViewModel.playlistName.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success->{
                    binding.txtAlbumName.text = it.data
                }
                is Resource.Error ->{
                    Toast.makeText(requireContext(),it.exception.message,Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading->{

                }

            }
        }

        singleViewModel.getPlaylistName(id)
    }


    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setLayout() {
        val userId = firebaseAuth.currentUser?.uid
        val userRef = firestore.collection("users")
        val query = userRef.whereEqualTo("userId", userId)

        query.get()
            .addOnSuccessListener { querySnapshot ->

                if (querySnapshot != null && !querySnapshot.isEmpty) {

                    val result = querySnapshot.documents[0]
                    val gender = result.getString("gender")
                    val img = result.getString("img")
                    val name = result.getString("username")

                    binding.txtArtistName.text = name ?: "N/A"

                    if (img?.isEmpty() == true) {
                        binding.imgArtist.setImageResource(if (gender == "Men") R.drawable.man_icon else R.drawable.woman_icon)
                    } else {
                        Glide.with(binding.root)
                            .load(img.toString())
                            .into(binding.imgArtist)
                    }
                } else {
                    binding.txtArtistName.text = "N/A"
                }

            }
    }

}