package com.example.spotifyclone.ui.fragments.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.SettingsAdapter
import com.example.spotifyclone.databinding.FragmentSettingsBinding
import com.example.spotifyclone.model.SettingItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        setNavigation()
        setLayout()
        setAdapter()
        return binding.root
    }


    private fun setNavigation() {
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.viewAccount.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_userLibraryFragment)
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
                        val image = querySnapshot.documents[0].getString("img")
                        val name = querySnapshot.documents[0].getString("username")

                        if (image?.isEmpty() == true) {
                            binding.imgProfile.setImageResource(if (gender == "Men") R.drawable.man_icon else R.drawable.woman_icon)
                        } else {
                            Glide.with(binding.root)
                                .load(image)
                                .into(binding.imgProfile)
                        }

                        binding.txtAccountName.text = name


                    } else {
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error retrieving user data: ${exception.message}")
                }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }


    private fun setAdapter() {
        val settingsItem = listOf(
            SettingItem("Account"),
            SettingItem("Data Saver"),
            SettingItem("Languages", true),
            SettingItem("Playback"),
            SettingItem("Explicit Content"),
            SettingItem("Devices"),
            SettingItem("Car"),
            SettingItem("Social"),
            SettingItem("Storage"),
            SettingItem("Log out", isLogout = true)
        )
        val adapter = SettingsAdapter({ findNavController().navigate(it) }, { logout() })
        adapter.submitList(settingsItem)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.adapter = adapter
    }


    private fun logout() {
        firebaseAuth.signOut()
        findNavController().navigate(R.id.action_settingsFragment_to_startFragment)
    }

}