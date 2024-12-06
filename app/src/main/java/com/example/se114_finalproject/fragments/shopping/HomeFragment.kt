package com.example.se114_finalproject.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.se114_finalproject.R
import com.example.se114_finalproject.adapters.HomeViewAdapter
import com.example.se114_finalproject.databinding.FragmentHomeBinding
import com.example.se114_finalproject.fragments.categories.AccessoriesFragment
import com.example.se114_finalproject.fragments.categories.CosmeticsFragment
import com.example.se114_finalproject.fragments.categories.EntertainmentFragment
import com.example.se114_finalproject.fragments.categories.FurnitureFragment
import com.example.se114_finalproject.fragments.categories.MainCategory
import com.example.se114_finalproject.fragments.categories.TechnologyFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoriesFragments = arrayListOf(
            MainCategory(),
            AccessoriesFragment(),
            CosmeticsFragment(),
            EntertainmentFragment(),
            FurnitureFragment(),
            TechnologyFragment()
        )

        val viewPager2Adapter =
            HomeViewAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Accessories"
                2 -> tab.text = "Cosmetic"
                3 -> tab.text = "Entertainment"
                4 -> tab.text = "Furniture"
                5 -> tab.text = "Technology"
            }
        }.attach()
    }
}