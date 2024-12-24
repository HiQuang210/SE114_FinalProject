package com.example.se114_finalproject.utilities

import androidx.fragment.app.Fragment
import com.example.se114_finalproject.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.example.se114_finalproject.R.id.bottomNav
        )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.example.se114_finalproject.R.id.bottomNav
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}