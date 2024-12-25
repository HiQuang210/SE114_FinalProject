package com.example.se114_finalproject.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.se114_finalproject.R
import com.example.se114_finalproject.databinding.ActivityShoppingBinding
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.viewmodel.CartVM
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityShoppingBinding.inflate(layoutInflater) }
    val viewModel by viewModels<CartVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController = (supportFragmentManager.findFragmentById(R.id.shoppingHostFragment) as NavHostFragment).navController
        binding.bottomNav.setupWithNavController(navController)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            val count = it.data?.size ?: 0
                            val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNav)
                            val badge = bottomNavigation.getBadge(R.id.cartFragment)

                            if (count > 0) {
                                bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                                    number = count
                                    backgroundColor = ContextCompat.getColor(this@ShoppingActivity, R.color.blue)
                                }
                            } else {
                                badge?.isVisible = false
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
