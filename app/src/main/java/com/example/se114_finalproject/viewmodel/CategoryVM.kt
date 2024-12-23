package com.example.se114_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.se114_finalproject.data.Category
import com.example.se114_finalproject.data.Product
import com.example.se114_finalproject.utilities.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryVM constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {
    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _newProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val newProducts = _newProducts.asStateFlow()

    init {
        fetchOfferProducts()
        fetchNewProducts()
    }

    private fun fetchNewProducts() {
        viewModelScope.launch {
            _newProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", category.category)
            .whereEqualTo("discountPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _newProducts.emit(Resource.Success(products))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _newProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }


    private fun fetchOfferProducts() {
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", category.category)
            .whereNotEqualTo("discountPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}
