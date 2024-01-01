package com.example.spotifyclone.ui.fragments.login

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentLoginBinding

class Login : Fragment() {
    private lateinit var binding : FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        setNavigation()
        setInputType()


        return binding.root
    }

    private fun setNavigation(){
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_homeFragment)
        }
        binding.navBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setInputType(){
        var type = false
        binding.imgVisibility.setOnClickListener {
            type = !type
            binding.edtPassword.inputType = if(type) InputType.TYPE_CLASS_TEXT else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

}