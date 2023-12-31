package com.example.spotifyclone.Search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.spotifyclone.Adapters.SearchCardAdapter
import com.example.spotifyclone.R
import com.example.spotifyclone.SearchCard
import com.example.spotifyclone.databinding.FragmentSearchBinding
import com.example.spotifyclone.databinding.ItemSearchCardBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        setAdapter()
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
}