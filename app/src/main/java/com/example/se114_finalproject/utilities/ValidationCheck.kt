package com.example.se114_finalproject.utilities

import android.util.Patterns

fun validateEmail(email: String) : RegisterValidation{
    if (email.isEmpty())
        return RegisterValidation.Failed("Please enter your email.")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Incorrect email format.")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation{
    if(password.isEmpty())
        return RegisterValidation.Failed("Please enter your password.")
    if(password.length < 8)
        return RegisterValidation.Failed("Password must contain more than 8 characters.")

    return RegisterValidation.Success
}