package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edtSearch.requestFocus()
        val inputMethodManager =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT)
    }
    private fun setNavigation(){
        binding.txtCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}