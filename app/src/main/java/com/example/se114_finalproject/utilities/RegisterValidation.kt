package com.example.se114_finalproject.utilities

sealed class RegisterValidation {
    data object Success: RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}

data class RegisterFieldState(
    val firstName: RegisterValidation,
    val lastName: RegisterValidation,
    val email : RegisterValidation,
    val password: RegisterValidation,
    val confirmPassword: RegisterValidation

)