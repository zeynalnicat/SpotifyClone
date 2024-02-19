package com.example.spotifyclone.ui.fragments.library

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide

import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.PlaylistAdapter
import com.example.spotifyclone.databinding.FragmentUserLibraryBinding

import com.example.spotifyclone.model.dto.PlaylistModel
import com.example.spotifyclone.resource.Resource
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
        setLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userLibraryViewModel.getPlaylists()
        userLibraryViewModel.playlists.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    setAdapter(it.data)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {

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
                            binding.imgProfile.setImageResource(if (gender == "Men") R.drawable.man_icon else R.drawable.woman_icon)
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

    private fun setAdapter(list: List<PlaylistModel>) {
        val adapter =
            PlaylistAdapter { bundle ->
                findNavController().navigate(
                    R.id.action_userLibraryFragment_to_singlePlaylistFragment,
                    bundle
                )
            }
        adapter.submitList(list)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter

    }

}