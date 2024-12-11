package com.example.se114_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.se114_finalproject.data.Product
import com.example.se114_finalproject.utilities.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryVM @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel(){

    private val _randomProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val randomProducts: StateFlow<Resource<List<Product>>> = _randomProducts

    init {
        fetchRandomProducts()
    }

    fun fetchRandomProducts() {
        viewModelScope.launch {
            _randomProducts.value = Resource.Loading()
        }
        firestore.collection("Products")
            .get()
            .addOnSuccessListener { result ->
                try {
                    val productList = result.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)?.copy(id = doc.id)
                    }.shuffled()
                        .take(5)

                    _randomProducts.value = Resource.Success(productList)
                } catch (e: Exception) {
                    _randomProducts.value = Resource.Error("Failed to parse products: ${e.message}")
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _randomProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}