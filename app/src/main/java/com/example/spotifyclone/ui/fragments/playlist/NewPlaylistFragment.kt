package com.example.spotifyclone.ui.fragments.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.data.sp.SharedPreference
import com.example.spotifyclone.databinding.FragmentNewPlaylistBinding
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.fragments.playlist.viewmodel.NewPlaylistFactory
import com.example.spotifyclone.ui.fragments.playlist.viewmodel.NewPlaylistViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentNewPlaylistBinding

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var activity: MainActivity

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private val newPlaylistViewModel: NewPlaylistViewModel by viewModels {
        NewPlaylistFactory(
            firebaseAuth,
            firestore
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater)
        activity = requireActivity() as MainActivity
        activity.setMusicPlayer(false)
        activity.setBottomNavigation(false)
        setNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreate.setOnClickListener {
            val name = binding.edtPlaylistName.text.toString()
            newPlaylistViewModel.insert(name)
        }
        newPlaylistViewModel.isSuccessful.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                activity.setBottomNavigation(true)
                findNavController().navigate(R.id.action_newPlaylistFragment_to_userLibraryFragment)
            } else if (it is Resource.Error) {
                Toast.makeText(requireContext(), it.exception.message, Toast.LENGTH_SHORT).show()
            }
        }

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val sharedPreference = SharedPreference(requireContext())
                activity.setBottomNavigation(true)
                if (sharedPreference.getValue("isPlaying", false)) {
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
        val sharedPreference = SharedPreference(requireContext())
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
            activity.setBottomNavigation(true)
            if (sharedPreference.getValue("isPlaying", false)) {
                activity.setMusicPlayer(true)
            }

        }
    }
}


