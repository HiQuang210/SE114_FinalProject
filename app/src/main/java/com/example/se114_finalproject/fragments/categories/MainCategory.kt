package com.example.se114_finalproject.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.se114_finalproject.R
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.randomProducts.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            showLoading()
                        }
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
        }
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
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = randomProductsAdapter
        }
    }
}