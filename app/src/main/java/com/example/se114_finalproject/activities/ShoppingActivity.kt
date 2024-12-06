package com.example.se114_finalproject.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.se114_finalproject.R
import com.example.se114_finalproject.databinding.ActivityShoppingBinding

class ShoppingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityShoppingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController = (supportFragmentManager.findFragmentById(R.id.shoppingHostFragment) as NavHostFragment).navController
        binding.bottomNav.setupWithNavController(navController)
    }
}
