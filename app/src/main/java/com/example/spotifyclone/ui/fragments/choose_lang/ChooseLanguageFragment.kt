package com.example.spotifyclone.ui.fragments.choose_lang

import LanguageHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentChooseLanguageBinding


class ChooseLanguageFragment : Fragment() {
    private lateinit var binding:FragmentChooseLanguageBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseLanguageBinding.inflate(inflater)
        setNavigation()
        setLanguage()
        return binding.root
    }

    private fun setNavigation(){
        binding.navBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setLanguage() {
        val savedLanguage = LanguageHelper.getSavedLanguage(requireContext())

        when (savedLanguage) {
            "en" -> binding.radioGroup.check(R.id.radioEnglish)
            "az" -> binding.radioGroup.check(R.id.radioAzerbaijan)
            "tr" -> binding.radioGroup.check(R.id.radioTurkish)
            else -> binding.radioGroup.check(R.id.radioEnglish)
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioEnglish -> LanguageHelper.setAndSaveLocale(requireContext(), "en")
                R.id.radioAzerbaijan -> LanguageHelper.setAndSaveLocale(requireContext(), "az")
                R.id.radioTurkish -> LanguageHelper.setAndSaveLocale(requireContext(), "tr")
            }
            requireActivity().recreate()
        }
    }

}