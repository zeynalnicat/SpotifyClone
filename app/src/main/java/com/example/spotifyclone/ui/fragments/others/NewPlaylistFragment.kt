package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentNewPlaylistBinding
import com.example.spotifyclone.ui.activity.MainActivity

class NewPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentNewPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater)
        val activity = requireActivity() as MainActivity
        activity.setMusicPlayer(false)
        activity.setBottomNavigation(false)
        setNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edtPlaylistName.requestFocus()
        val inputMethodManager =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.edtPlaylistName, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setNavigation() {
        val activity = requireActivity() as MainActivity

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
            activity.setMusicPlayer(true)
            activity.setBottomNavigation(true)
        }
    }
}


