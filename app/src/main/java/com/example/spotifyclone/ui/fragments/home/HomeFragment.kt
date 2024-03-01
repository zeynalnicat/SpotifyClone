package com.example.spotifyclone.ui.fragments.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentHomeBinding
import com.example.spotifyclone.network.deezer.TrackApi
import com.example.spotifyclone.network.retrofit.TokenRefresher
import com.example.spotifyclone.network.retrofit.api.AlbumApi
import com.example.spotifyclone.network.retrofit.api.ArtistsApi
import com.example.spotifyclone.ui.fragments.home.HomeFactory
import com.example.spotifyclone.ui.fragments.home.HomeViewModel
import com.example.spotifyclone.ui.fragments.home.general.FragmentPageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: FragmentPageAdapter

    @Inject
    lateinit var artistsApi: ArtistsApi

    @Inject
    lateinit var albumApi: AlbumApi


    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var tokenRefresher: TokenRefresher

    private lateinit var trackApi: TrackApi


    private val homeViewModel: HomeViewModel by viewModels {
        HomeFactory(
            albumApi,
            artistsApi,
            firestore,
            firebaseAuth,
            tokenRefresher,
            trackApi
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        trackApi = retrofit.create(TrackApi::class.java)
        setLayout()
        setTextHeader()
        setDrawer()
        setNavigation()
        adapter = FragmentPageAdapter(
            requireActivity().supportFragmentManager,
            requireActivity().lifecycle
        )
        binding.viewPager.isUserInputEnabled = false

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getText(R.string.home_txt_all)))
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(getText(R.string.home_btn_music))
        )

        binding.viewPager.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    binding.viewPager.currentItem = it.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
        return binding.root
    }


    private fun setLayout() {
        val userRef = firestore.collection("users")
        val query = userRef.whereEqualTo("userId", firebaseAuth.currentUser?.uid)
        try {
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val gender = querySnapshot.documents[0].getString("gender")
                        val name = querySnapshot.documents[0].getString("username")
                        val image = querySnapshot.documents[0].getString("img")

                        binding.txtNameDrawer.text = name ?: "N/A"
                        if (image?.isEmpty() == true) {
                            binding.imgProfileAccount.setImageResource(if (gender == "Men") R.drawable.man_icon else R.drawable.woman_icon)
                            binding.imgProfileDrawer.setImageResource(if (gender == "Men") R.drawable.man_icon else R.drawable.woman_icon)
                        } else {
                            Glide.with(binding.root)
                                .load(image.toString())
                                .into(binding.imgProfileAccount)

                            Glide.with(binding.root)
                                .load(image.toString())
                                .into(binding.imgProfileDrawer)
                        }
                    } else {
                        binding.txtNameDrawer.text = "N/A"
                    }
                }
                .addOnFailureListener { exception ->
                    binding.txtNameDrawer.text = "N/A"
                    Log.e("Firestore", "Error retrieving user data: ${exception.message}")
                }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun setTextHeader() {
        homeViewModel.setDateText()
        homeViewModel.date.observe(viewLifecycleOwner) {
//            binding.txtGood.text = it
        }
    }

    private fun setNavigation() {
        binding.viewProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_userLibraryFragment)
        }

        binding.viewSetting.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    private fun setDrawer() {
        binding.imgProfileAccount.setOnClickListener {
            if (binding.drawer2.isDrawerOpen(GravityCompat.START)) {
                binding.drawer2.closeDrawer(GravityCompat.START)
            } else {
                binding.drawer2.openDrawer(GravityCompat.START)
            }
        }
    }


}