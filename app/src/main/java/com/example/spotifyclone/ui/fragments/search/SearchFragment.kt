package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.adapters.SearchCardAdapter
import com.example.spotifyclone.databinding.FragmentSearchBinding
import com.example.spotifyclone.viewmodels.SearchViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
    }

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

        searchViewModel.getCategories()
        searchViewModel.categories.observe(viewLifecycleOwner) {
            val adapter = SearchCardAdapter()
            adapter.submitList(it)
            binding.recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2)
            binding.recyclerView.adapter = adapter
        }


    }

    private fun setNavigation() {
        binding.edtSearch.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchNextFragment)
        }
    }
}