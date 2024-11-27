package com.example.se114_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.se114_finalproject.data.User
import com.example.se114_finalproject.utilities.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _googleSignInState = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val googleSignInState: Flow<Resource<User>> = _googleSignInState
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch { _login.emit(Resource.Loading()) }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                viewModelScope.launch {
                    authResult.user?.let {
                        _login.emit(Resource.Success(it))
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _login.emit(Resource.Error(exception.message.toString()))
                }
            }
    }

    fun loginWithGoogle(credential: AuthCredential) {
        viewModelScope.launch { _login.emit(Resource.Loading()) }

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                viewModelScope.launch {
                    authResult.user?.let {
                        _login.emit(Resource.Success(it))
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _login.emit(Resource.Error(exception.message.toString()))
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}
