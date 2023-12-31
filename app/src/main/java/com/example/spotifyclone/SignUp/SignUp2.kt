package com.example.spotifyclone.SignUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.spotifyclone.R
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import com.example.spotifyclone.databinding.FragmentSignUp1Binding



class SignUp2 : Fragment() {

    private lateinit var binding: FragmentSignUp1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUp1Binding.inflate(inflater,container,false)
        val view = binding.root
        adaptLayout()
        binding.btnNext.setOnClickListener{
            if(binding.edtEmail.text.toString().length>=8){
                binding.btnNext.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.white
                ))
                Navigation.findNavController(it).navigate(R.id.toSignUp3)
            }

        }

        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.txtNeed.visibility = if (s?.length ?: 0 >= 8) View.INVISIBLE else View.VISIBLE
            }

        })

        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),
                R.id.fragmentContainerView
            )
            navController.popBackStack(R.id.signUp1, false)
        }


        return view
    }

    private fun adaptLayout(){
        binding.edtEmail.inputType=InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.txtNeed.text = "Use at least 8 characters."
        binding.labelEdt.text = "Create a password"
        binding.edtEmail.inputType =InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }


}