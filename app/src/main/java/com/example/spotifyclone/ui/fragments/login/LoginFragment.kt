package com.example.spotifyclone.ui.fragments.login

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)

        setNavigation()
        setInputType()
        setLogin()


        return binding.root
    }


    private fun setLogin() {
        binding.btnNext.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(requireView(), "Fill the credentials", Snackbar.LENGTH_SHORT).show()
            } else {
                try {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            findNavController().navigate(R.id.action_login_to_homeFragment)
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setNavigation() {

        binding.navBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setInputType() {
        var type = false
        binding.imgVisibility.setOnClickListener {
            type = !type
            binding.edtPassword.inputType =
                if (type) InputType.TYPE_CLASS_TEXT else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

}