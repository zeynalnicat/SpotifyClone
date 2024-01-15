package com.example.spotifyclone.ui.fragments.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {
      private lateinit var binding:FragmentLibraryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater)
        setNavigation()
        setDrawer()
        return binding.root
    }

    private fun setNavigation(){

        binding.viewProfile.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_userLibraryFragment)
        }

        binding.viewSetting.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_settingsFragment)
        }
    }

    private fun setDrawer(){
       binding.imgProfileAccount.setOnClickListener {
           if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
               binding.drawer.closeDrawer(GravityCompat.START)
           } else {
               binding.drawer.openDrawer(GravityCompat.START)
           }
       }
    }


}