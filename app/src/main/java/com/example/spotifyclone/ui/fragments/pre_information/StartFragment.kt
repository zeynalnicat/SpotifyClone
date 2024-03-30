package com.example.spotifyclone.ui.fragments.pre_information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentStartBinding
import com.example.spotifyclone.ui.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        val view = binding.root

        val activity = requireActivity() as MainActivity
        activity.setBottomNavigation(false)

        binding.btnSign.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.toSignUp1)
        }

        binding.txtLogin.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.toLogin)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseAuth.currentUser != null) {
            findNavController().navigate(R.id.action_startFragment_to_homeFragment)
        }

    }


}