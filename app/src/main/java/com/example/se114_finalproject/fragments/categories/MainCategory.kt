package com.example.se114_finalproject.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.se114_finalproject.R
import com.example.se114_finalproject.adapters.BestDealsAdapter
import com.example.se114_finalproject.adapters.NewProductsAdapter
import com.example.se114_finalproject.adapters.RandomProductsAdapter
import com.example.se114_finalproject.databinding.FragmentMainCategoryBinding
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.viewmodel.MainCategoryVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainCategory : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var randomProductsAdapter: RandomProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var newProductsAdapter: NewProductsAdapter
    private val viewModel by viewModels<MainCategoryVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRandomProductRv()
        setupBestDealsRv()
        setupNewProductsRv()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.randomProducts.collectLatest {
                        when (it) {
                            is Resource.Loading -> showLoading()
                            is Resource.Success -> {
                                randomProductsAdapter.differ.submitList(it.data)
                                hideLoading()
                            }
                            is Resource.Error -> {
                                hideLoading()
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.bestDeals.collectLatest {
                        when (it) {
                            is Resource.Loading -> showLoading()
                            is Resource.Success -> {
                                bestDealsAdapter.differ.submitList(it.data)
                                hideLoading()
                            }
                            is Resource.Error -> {
                                hideLoading()
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> Unit
                        }
                    }
                }
                launch {
                    viewModel.newProducts.collectLatest {
                        when (it) {
                            is Resource.Loading ->
                                binding.newProductsProgressbar.visibility = View.VISIBLE
                            is Resource.Success -> {
                                newProductsAdapter.differ.submitList(it.data)
                                binding.newProductsProgressbar.visibility = View.GONE
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                                binding.newProductsProgressbar.visibility = View.GONE
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                viewModel.fetchNewProducts()
            }
        })
    }

    private fun showLoading() {
        binding.mainCategoryProgressbar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.mainCategoryProgressbar.visibility = View.GONE
    }
    private fun setupRandomProductRv() {
        randomProductsAdapter = RandomProductsAdapter()
        binding.rvRandomProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = randomProductsAdapter
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun setupNewProductsRv() {
        newProductsAdapter = NewProductsAdapter()
        binding.rvNewProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = newProductsAdapter
        }
    }
}