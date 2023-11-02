package com.example.spotifyclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.spotifyclone.databinding.FragmentSignUp3Binding
import com.example.spotifyclone.databinding.FragmentSignUp4Binding

class SignUp4 : Fragment() {
    private lateinit var binding: FragmentSignUp4Binding
    private lateinit var btnList:MutableList<Button>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUp4Binding.inflate(inflater, container, false)

        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
            navController.popBackStack(R.id.signUp3, false)
        }

        return binding.root
    }
}