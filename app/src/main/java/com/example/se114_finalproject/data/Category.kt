package com.example.se114_finalproject.data

sealed class Category(val category: String) {

    object Accessories: Category("Accessories")
    object Cosmetics: Category("Cosmetics")
    object Entertainment: Category("Table")
    object Furniture: Category("Furniture")
    object Technology: Category("Technology")
}
