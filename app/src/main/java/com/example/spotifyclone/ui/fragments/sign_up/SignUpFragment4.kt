package com.example.spotifyclone.ui.fragments.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSignUp4Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignUpFragment4 : Fragment() {
    private lateinit var binding: FragmentSignUp4Binding

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    private var gender = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUp4Binding.inflate(inflater, container, false)

        arguments?.let {
            gender = it.getString("gender", "")
        }

        setLogin()

        setNavigation()

        return binding.root
    }

    private fun setLogin() {
        val userRef = firestore.collection("users")
        val userId = firebaseAuth.currentUser?.uid.toString()
        binding.createAcc.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val user = hashMapOf(
                "gender" to gender,
                "img" to "",
                "userId" to userId,
                "username" to username
            )
            try {
                userRef.add(user)
                    .addOnSuccessListener {
                        findNavController().navigate(R.id.toChooseArtist)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }

        }


    }


    private fun setNavigation() {
        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(
                requireActivity(),
                R.id.fragmentContainerView
            )
            navController.popBackStack(R.id.signUp3, false)
        }

    }
}