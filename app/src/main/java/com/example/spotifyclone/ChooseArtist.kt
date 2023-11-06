package com.example.spotifyclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.spotifyclone.Adapters.ArtistsAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.example.spotifyclone.databinding.FragmentChooseArtistBinding
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.JustifyContent

class ChooseArtist : Fragment() {
    private lateinit var binding: FragmentChooseArtistBinding
    private lateinit var artistsList: MutableList<Artists>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseArtistBinding.inflate(inflater, container, false)


        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.SPACE_BETWEEN
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.FLEX_START
        binding.artistRecycle.layoutManager = layoutManager
        artistsList = mutableListOf(
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"), Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),
            Artists(R.drawable.billie, "Billie Eilish"),
            Artists(R.drawable.ariaa, "Ariana Grande"),
            Artists(R.drawable.kanye, "Kanye West"),

            )


        binding.navBack.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
            navController.popBackStack(R.id.signUp43, false)
        }

        val adapter = ArtistsAdapter(artistsList)
        binding.artistRecycle.adapter = adapter


        return binding.root
    }

}