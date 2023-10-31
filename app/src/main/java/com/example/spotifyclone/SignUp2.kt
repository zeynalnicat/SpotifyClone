package com.example.spotifyclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation

import com.example.spotifyclone.databinding.FragmentSignUp2Binding



class SignUp2 : Fragment() {

    private lateinit var binding: FragmentSignUp2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUp2Binding.inflate(inflater,container,false)
        val view = binding.root

        binding.btnNext.setOnClickListener{
            if(binding.edtPassword.text.toString().length>=8){
                binding.btnNext.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                Navigation.findNavController(it).navigate(R.id.toSignUp3)
            }

        }

        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
            navController.popBackStack(R.id.signUp1, false)
        }


        return view
    }


}