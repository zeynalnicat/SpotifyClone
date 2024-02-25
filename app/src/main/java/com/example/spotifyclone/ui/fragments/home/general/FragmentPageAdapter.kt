package com.example.spotifyclone.ui.fragments.home.general

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spotifyclone.ui.fragments.home.FragmentHomeGeneral
import com.example.spotifyclone.ui.fragments.home.music.HomeFragmentMusic

class FragmentPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            FragmentHomeGeneral()
        } else {
            HomeFragmentMusic()
        }
    }

}