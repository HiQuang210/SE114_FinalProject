package com.example.se114_finalproject.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.se114_finalproject.data.Category
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.viewmodel.CategoryVM
import com.example.se114_finalproject.viewmodel.factory.BaseCategoryVMFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EntertainmentFragment : BaseCategoryFragment(){

    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryVM> {
        BaseCategoryVMFactory(firestore, Category.Entertainment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect offerProducts
                launch {
                    viewModel.offerProducts.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                showOfferLoading()
                            }
                            is Resource.Success -> {
                                offerAdapter.differ.submitList(it.data)
                                hideOfferLoading()
                            }
                            is Resource.Error -> {
                                Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                                hideOfferLoading()
                            }
                            else -> Unit
                        }
                    }
                }

                // Collect newProducts
                launch {
                    viewModel.newProducts.collectLatest {
                        when (it) {
                            is Resource.Loading -> {
                                showNewProductsLoading()
                            }
                            is Resource.Success -> {
                                newProductsAdapter.differ.submitList(it.data)
                                hideNewProductsLoading()
                            }
                            is Resource.Error -> {
                                Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                                    .show()
                                hideNewProductsLoading()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    override fun onOfferPagingRequest() {}

    override fun onNewProductsPagingRequest() {}
}