package com.example.spotifyclone.ui.fragments.sign_up

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSignUp1Binding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignUpFragment2 : Fragment() {

    private lateinit var binding: FragmentSignUp1Binding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUp1Binding.inflate(inflater, container, false)
        val view = binding.root

        arguments?.let {
            email = it.getString("email", "")
        }
        adaptLayout()
        setNavigation()
        setLogin()
        setInput()
        return view
    }


    private fun setLogin() {
        binding.btnNext.setOnClickListener {
            val password = binding.edtEmail.text.toString()
            when {
                password.isEmpty() -> Snackbar.make(
                    requireView(),
                    R.string.sign_missing_password,
                    Snackbar.LENGTH_SHORT
                ).view

                email.isEmpty() -> Snackbar.make(
                    requireView(),
                    R.string.sign_missing_email,
                    Snackbar.LENGTH_SHORT
                ).view

                email.isEmpty() && password.isEmpty() -> Snackbar.make(
                    requireView(),
                    R.string.sign_missing_both,
                    Snackbar.LENGTH_SHORT
                ).view

                else -> {
                    try {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                findNavController().navigate(R.id.signUp3)
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }

    }

    private fun adaptLayout() {
        binding.edtEmail.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.txtNeed.setText(R.string.sign_use_at_least)
        binding.labelEdt.setText(R.string.sign_create_password)
        binding.edtEmail.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }


    private fun setNavigation() {
        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(
                requireActivity(),
                R.id.fragmentContainerView
            )
            navController.popBackStack(R.id.signUp1, false)
        }

    }

    private fun setInput() {

        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.txtNeed.visibility =
                    if (s?.length ?: 0 >= 8) View.INVISIBLE else View.VISIBLE
            }

        })
    }
}