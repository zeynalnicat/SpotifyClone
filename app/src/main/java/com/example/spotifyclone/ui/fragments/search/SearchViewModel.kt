package com.example.spotifyclone.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.spotifyclone.model.dto.Category
import com.example.spotifyclone.resource.Resource
import com.google.firebase.firestore.FirebaseFirestore


class SearchViewModel(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _categories = MutableLiveData<Resource<List<Category>>>()

    val categories: LiveData<Resource<List<Category>>>
        get() = _categories


    fun getCategories() {
        val categoryRef = firestore.collection("categories")
        categoryRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val listCategory = mutableListOf<Category>()
                    val documents = querySnapshot.documents
                    for (document in documents) {
                        val category = Category(
                            document["name"].toString(),
                            document["color"].toString(),
                            document["img"].toString(),
                            document["tracks"] as List<String>
                        )
                        listCategory.add(category)
                    }
                    _categories.postValue(Resource.Success(listCategory))
                }
            }
            .addOnFailureListener {
                _categories.postValue(Resource.Error(it))
            }
    }
}