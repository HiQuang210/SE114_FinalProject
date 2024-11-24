package com.example.se114_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.se114_finalproject.utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordVM @Inject constructor(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _resetPassword = MutableSharedFlow<Resource<Unit>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun sendResetPasswordEmail(email: String) {
        viewModelScope.launch { _resetPassword.emit(Resource.Loading()) }
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(Unit))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}
