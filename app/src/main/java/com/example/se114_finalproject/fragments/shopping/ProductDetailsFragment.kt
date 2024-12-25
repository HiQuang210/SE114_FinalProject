package com.example.se114_finalproject.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.se114_finalproject.R
import com.example.se114_finalproject.adapters.ColorsAdapter
import com.example.se114_finalproject.adapters.PreviewImagesAdapter
import com.example.se114_finalproject.adapters.SizesAdapter
import com.example.se114_finalproject.data.Cart
import com.example.se114_finalproject.databinding.FragmentProductDetailsBinding
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.utilities.hideBottomNavigationView
import com.example.se114_finalproject.viewmodel.ProductDetailsVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val previewImgAdapter by lazy { PreviewImagesAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<ProductDetailsVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupPreviewImg()

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "${product.price} VND"
            tvProductDescription.text = product.description

            if (product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE
            if (product.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE
        }

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        binding.buttonAddToCart.setOnClickListener {
            when {
                product.sizes != null && selectedSize == null -> {
                    Toast.makeText(requireContext(), "Please select a size.", Toast.LENGTH_SHORT).show()
                }
                product.colors != null && selectedColor == null -> {
                    Toast.makeText(requireContext(), "Please select a color.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.addUpdateProductInCart(Cart(product, 1, selectedColor, selectedSize))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToCart.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonAddToCart.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.buttonAddToCart.revertAnimation()
                            binding.buttonAddToCart.setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.skyblue)
                            )
                        }
                        is Resource.Error -> {
                            binding.buttonAddToCart.stopAnimation()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        previewImgAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }
    }


    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupPreviewImg() {
        binding.apply {
            viewPagerProductImages.adapter = previewImgAdapter
        }
    }
}