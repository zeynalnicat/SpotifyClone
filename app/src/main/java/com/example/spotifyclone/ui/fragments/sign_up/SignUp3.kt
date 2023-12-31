package com.example.spotifyclone.ui.fragments.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSignUp3Binding

class SignUp3 : Fragment() {
    private lateinit var binding: FragmentSignUp3Binding
    private lateinit var btnList:MutableList<Button>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUp3Binding.inflate(inflater, container, false)

        btnList = mutableListOf()
        btnList.add(binding.btnMen)
        btnList.add(binding.btnWomen)
        btnList.add(binding.btnOther)

        for (btn in btnList) {
            btn.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(R.id.toSignUp4)
            }
        }

        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),
                R.id.fragmentContainerView
            )
            navController.popBackStack(R.id.signUp2, false)
        }

        return binding.root
    }
}
