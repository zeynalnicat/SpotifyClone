package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSearchNextBinding


class SearchNextFragment : Fragment() {
    private lateinit var binding:FragmentSearchNextBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchNextBinding.inflate(inflater)
        setNavigation()
        return binding.root
    }

    private fun setNavigation(){
        binding.txtCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}