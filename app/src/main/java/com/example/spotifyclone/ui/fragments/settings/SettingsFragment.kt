package com.example.spotifyclone.ui.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        setNavigation()
        return binding.root
    }


    private fun setNavigation(){
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}