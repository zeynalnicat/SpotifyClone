package com.example.spotifyclone.ui.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentNewPlaylistBinding
import com.example.spotifyclone.db.RoomDB
import com.example.spotifyclone.resource.Resource
import com.example.spotifyclone.sp.SharedPreference
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.viewmodels.NewPlaylistViewModel
import com.example.spotifyclone.viewmodels.factories.NewPlaylistFactory
import com.google.android.material.snackbar.Snackbar

class NewPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentNewPlaylistBinding
    private lateinit var roomDB: RoomDB
    private val newPlaylistViewModel: NewPlaylistViewModel by viewModels { NewPlaylistFactory(roomDB) }

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
        roomDB = RoomDB.accessDb(requireContext())!!
        binding.btnCreate.setOnClickListener {
            val name = binding.edtPlaylistName.text.toString()
            newPlaylistViewModel.insert(name)
        }
        newPlaylistViewModel.isSuccessful.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                findNavController().navigate(R.id.action_newPlaylistFragment_to_userLibraryFragment)
            } else if (it is Resource.Error) {
                Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT).show()
            }
        }

        val callBack = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val sharedPreference = SharedPreference(requireContext())
                val activity = requireActivity() as MainActivity
                activity.setBottomNavigation(true)
                if(sharedPreference.getValue("isPlaying",false)){
                    activity.setMusicPlayer(true)
                }
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)
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


