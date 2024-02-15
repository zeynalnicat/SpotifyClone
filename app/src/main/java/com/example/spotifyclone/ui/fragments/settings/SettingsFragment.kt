package com.example.spotifyclone.ui.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.SettingsAdapter
import com.example.spotifyclone.databinding.FragmentSettingsBinding
import com.example.spotifyclone.model.SettingItem


class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        setNavigation()
        setAdapter()
        return binding.root
    }


    private fun setNavigation(){
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.viewAccount.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_userLibraryFragment)
        }
    }


    private fun setAdapter(){
        val settingsItem = listOf(
            SettingItem("Account"),
            SettingItem("Data Saver"),
            SettingItem("Languages",true),
            SettingItem("Playback"),
            SettingItem("Explicit Content"),
            SettingItem("Devices"),
            SettingItem("Car"),
            SettingItem("Social"),
            SettingItem("Storage"),
            )
        val adapter = SettingsAdapter{findNavController().navigate(it)}
        adapter.submitList(settingsItem)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),1)
        binding.recyclerView.adapter = adapter
    }

}