package com.example.spotifyclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.spotifyclone.databinding.FragmentStartBinding


class StartFragment:Fragment() {
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentStartBinding.inflate(inflater,container,false)
        val view = binding.root

        binding.btnSign.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.toSignUp1)
        }

        binding.txtLogin.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.toLogin)
        }

        return view
    }


}