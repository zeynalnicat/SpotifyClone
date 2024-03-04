package com.example.spotifyclone.ui.fragments.sign_up

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSignUp1Binding
import com.google.android.material.snackbar.Snackbar

class SignUp1 : Fragment() {

    private lateinit var binding: FragmentSignUp1Binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUp1Binding.inflate(inflater, container, false)
        val view = binding.root

        binding.navBack.setOnClickListener {
            val navController = findNavController(
                requireActivity(),
                R.id.fragmentContainerView
            )
            navController.popBackStack(R.id.startFragment, false)
        }

        setLogin()



        return view
    }

    private fun setLogin() {

        binding.btnNext.setOnClickListener {
            val edtEmail = binding.edtEmail.text.toString()
            if (edtEmail.isEmpty()) {
                Snackbar.make(requireView(), R.string.sign_snackbar_email, Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                val bundle = Bundle()
                bundle.putString("email", edtEmail)
                findNavController(it).navigate(R.id.toSignUp2, bundle)
            }
        }
    }
}