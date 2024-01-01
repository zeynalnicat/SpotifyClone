package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.SearchCardAdapter
import com.example.spotifyclone.model.SearchCard
import com.example.spotifyclone.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        setAdapter()
        setNavigation()
        return binding.root
    }

    private fun setAdapter() {
        val cards = listOf(
            SearchCard("Pop"),
            SearchCard("Indie"),
            SearchCard("Comedy")
        )

        val adapter = SearchCardAdapter()
        adapter.submitList(cards)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter


        val popularCards = listOf(
            SearchCard("News & Politics"),
            SearchCard("Comedy")
        )
        val adapterPopular = SearchCardAdapter()
        adapterPopular.submitList(popularCards)
        binding.recyclerViewPopular.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewPopular.adapter = adapterPopular

        val allCards = listOf(
            SearchCard("Pop"),
            SearchCard("Indie"),
            SearchCard("Comedy"),
            SearchCard("News & Politics"),
        )
        val adapterAll = SearchCardAdapter()
        adapterAll.submitList(allCards)
        binding.recyclerViewAll.layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.HORIZONTAL,false)
        binding.recyclerViewAll.adapter = adapterAll
    }

    private fun setNavigation(){
        binding.edtSearch.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchNextFragment)
        }
    }
}