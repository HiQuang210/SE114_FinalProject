package com.example.se114_finalproject.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.se114_finalproject.R
import com.example.se114_finalproject.adapters.BestDealsAdapter
import com.example.se114_finalproject.adapters.NewProductsAdapter
import com.example.se114_finalproject.databinding.FragmentBaseCategoryBinding
import com.example.se114_finalproject.utilities.showBottomNavigationView

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category){
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestDealsAdapter by lazy { BestDealsAdapter() }
    protected val  newProductsAdapter: NewProductsAdapter by lazy { NewProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOfferRv()
        setupNewProductsRv()

        offerAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        newProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        binding.rvOfferProducts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1) && dx != 0){
                    onOfferPagingRequest()
                }
            }
        })
    }

    fun showOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.VISIBLE
    }

    fun hideOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.GONE
    }

    fun showNewProductsLoading(){
        binding.newProductsProgressBar.visibility = View.VISIBLE
    }

    fun hideNewProductsLoading(){
        binding.newProductsProgressBar.visibility = View.GONE
    }

    open fun onOfferPagingRequest(){}

    open fun onNewProductsPagingRequest(){}

    private fun setupOfferRv() {
        binding.rvOfferProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = offerAdapter
        }
    }

    private fun setupNewProductsRv() {
        binding.rvNewProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = newProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}