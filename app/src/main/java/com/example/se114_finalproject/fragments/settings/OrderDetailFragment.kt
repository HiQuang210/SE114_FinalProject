package com.example.se114_finalproject.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.se114_finalproject.adapters.BillingProductsAdapter
import com.example.se114_finalproject.data.order.OrderStatus
import com.example.se114_finalproject.data.order.getOrderStatus
import com.example.se114_finalproject.databinding.FragmentOrderDetailBinding
import com.example.se114_finalproject.utilities.VerticalItemDecoration
import com.example.se114_finalproject.utilities.hideBottomNavigationView

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = args.order
        hideBottomNavigationView()

        setupOrderRv()

        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"

            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone

            tvTotalPrice.text = "${order.totalPrice} VND"

        }

        billingProductsAdapter.differ.submitList(order.products)

        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}