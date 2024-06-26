package com.example.spotifyclone.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentSearchBinding
import com.example.spotifyclone.domain.resource.Resource
import com.example.spotifyclone.ui.activity.MainActivity
import com.example.spotifyclone.ui.adapters.SearchCardAdapter
import com.example.spotifyclone.ui.fragments.search.viewmodel.SearchFactory
import com.example.spotifyclone.ui.fragments.search.viewmodel.SearchViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding



    @Inject
    lateinit var firestore: FirebaseFirestore

    private val searchViewModel: SearchViewModel by viewModels { SearchFactory(firestore) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        setNavigation()
        val activity = requireActivity() as MainActivity
        activity.setBottomNavigation(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModel.categories.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> {
                    setAdapter(it.data)
                }
                is Resource.Error-> {

                }

                is Resource.Loading -> {

                }
            }
        }
        searchViewModel.getCategories()
    }

    private fun setAdapter(data: List<com.example.spotifyclone.domain.model.dto.Category>) {
            val adapter = SearchCardAdapter{findNavController().navigate(R.id.action_searchFragment_to_singleCategoryFragment,it)}
            adapter.submitList(data)
            binding.recyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2)
            binding.recyclerView.adapter = adapter



    }

    private fun setNavigation() {
        binding.edtSearch.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_searchNextFragment)
        }
    }
}