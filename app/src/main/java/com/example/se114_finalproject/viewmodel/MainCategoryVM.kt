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
class MainCategoryVM @Inject constructor(private val firestore: FirebaseFirestore) : ViewModel(){

    private val _randomProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val randomProducts: StateFlow<Resource<List<Product>>> = _randomProducts

    private val _bestDeals = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDeals: StateFlow<Resource<List<Product>>> = _bestDeals

    private val _newProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val newProducts: StateFlow<Resource<List<Product>>> = _newProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchRandomProducts()
        fetchBestDeals()
        fetchNewProducts()
    }

    private fun fetchRandomProducts() {
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

    private fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDeals.value = Resource.Loading()
        }
        firestore.collection("Products")
            .whereGreaterThan("discountPercentage", 0.25)
            .get()
            .addOnSuccessListener { result ->
                try {
                    val bestDealsList = result.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)?.copy(id = doc.id)
                    }
                    _bestDeals.value = Resource.Success(bestDealsList)
                } catch (e: Exception) {
                    _bestDeals.value = Resource.Error("Failed to parse best deals: ${e.message}")
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchNewProducts() {
        if (!pagingInfo.isPagingEnd) {
        viewModelScope.launch {
            _newProducts.value = Resource.Loading()
        }
        firestore.collection("Products")
            .orderBy("uploadedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(pagingInfo.newProductsPage * 10)
            .get()
            .addOnSuccessListener { result ->
                val newProductsList = result.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                if (newProductsList.isEmpty() || newProductsList == pagingInfo.oldNewProducts) {
                    pagingInfo.isPagingEnd = true
                    _newProducts.value = Resource.Success(pagingInfo.oldNewProducts) // Emit success with old data
                } else {
                    pagingInfo.oldNewProducts = newProductsList
                    _newProducts.value = Resource.Success(newProductsList)
                    pagingInfo.newProductsPage++
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _newProducts.emit(Resource.Error(it.message.toString()))
                }
            }
        }
    }
}

internal data class PagingInfo(
    var newProductsPage: Long = 1,
    var oldNewProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)