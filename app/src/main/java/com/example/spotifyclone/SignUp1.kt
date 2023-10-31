package com.example.spotifyclone

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.spotifyclone.databinding.FragmentSignUp1Binding

class SignUp1 : Fragment() {

    private lateinit var binding: FragmentSignUp1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUp1Binding.inflate(inflater,container,false)
        val view = binding.root


            binding.btnNext.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.toSignUp2)
            }

        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
            navController.popBackStack(R.id.startFragment, false)
        }



        return view
    }
}