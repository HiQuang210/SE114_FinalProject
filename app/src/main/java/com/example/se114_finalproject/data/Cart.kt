package com.example.se114_finalproject.data

data class Cart(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
) {
    constructor() : this(Product(), 1, null, null)
}
