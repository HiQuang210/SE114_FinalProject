package com.example.se114_finalproject.utilities

sealed class RegisterValidation {
    object Success: RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}

data class RegisterFieldState(
    val email : RegisterValidation,
    val password: RegisterValidation
)