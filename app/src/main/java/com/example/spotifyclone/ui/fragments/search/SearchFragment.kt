package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.ui.adapters.SearchCardAdapter
import com.example.spotifyclone.databinding.FragmentSearchBinding
import com.example.spotifyclone.network.retrofit.api.CategoriesApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding


    @Inject
    lateinit var categoriesApi: CategoriesApi

    private val searchViewModel: SearchViewModel by viewModels { SearchFactory(categoriesApi) }


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