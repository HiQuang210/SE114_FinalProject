@file:Suppress("DEPRECATION")

package com.example.se114_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import com.example.se114_finalproject.data.User
import com.example.se114_finalproject.utilities.Constants.USER_COLLECTION
import com.example.se114_finalproject.utilities.RegisterFieldState
import com.example.se114_finalproject.utilities.RegisterValidation
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.utilities.validateEmail
import com.example.se114_finalproject.utilities.validateFirstName
import com.example.se114_finalproject.utilities.validateLastName
import com.example.se114_finalproject.utilities.validatePassword
import com.example.se114_finalproject.utilities.validatePasswordMatch
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterVM @Inject constructor(private val firebaseAuth: FirebaseAuth, private val db: FirebaseFirestore) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    private val _successMessage = Channel<String>()
    val successMessage = _successMessage.receiveAsFlow()

    private val _googleSignInState = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val googleSignInState: Flow<Resource<User>> = _googleSignInState

    fun createAccountWithEmailAndPassword(user: User, password: String, confirmPassword: String) {
        val registerFieldState = RegisterFieldState(
            firstName = validateFirstName(user.firstName),
            lastName = validateLastName(user.lastName),
            email = validateEmail(user.email),
            password = validatePassword(password),
            confirmPassword = validatePasswordMatch(password, confirmPassword)
        )

        if (!isLocalValidationSuccessful(registerFieldState)) {
            runBlocking {
                _validation.send(registerFieldState)
            }
            return
        }

        db.collection(USER_COLLECTION)
            .whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    runBlocking {
                        _validation.send(
                            registerFieldState.copy(
                                email = RegisterValidation.Failed("This email is already registered.")
                            )
                        )
                    }
                } else {
                    registerUserInFirebase(user, password)
                }
            }
            .addOnFailureListener {
                _register.value = Resource.Error("Error checking email: ${it.message}")
            }
    }

    private fun registerUserInFirebase(user: User, password: String) {
        runBlocking {
            _register.emit(Resource.Loading())
        }
        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener {
                it.user?.let { firebaseUser ->
                    saveUserInfo(firebaseUser.uid, user)
                }
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun isLocalValidationSuccessful(registerFieldState: RegisterFieldState): Boolean {
        return registerFieldState.firstName is RegisterValidation.Success &&
                registerFieldState.lastName is RegisterValidation.Success &&
                registerFieldState.email is RegisterValidation.Success &&
                registerFieldState.password is RegisterValidation.Success &&
                registerFieldState.confirmPassword is RegisterValidation.Success
    }

    fun signInWithGoogle(credential: AuthCredential, account: GoogleSignInAccount) {
        _googleSignInState.value = Resource.Loading()

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val user = createUserFromGoogleAccount(account, firebaseUser)
                    saveUserInfo(firebaseUser.uid, user)
                }
            }
            .addOnFailureListener { exception ->
                _googleSignInState.value = Resource.Error("Google sign-in failed: ${exception.message}")
            }
    }

    private fun createUserFromGoogleAccount(
        account: GoogleSignInAccount,
        firebaseUser: FirebaseUser
    ): User {
        val firstName = account.givenName ?: ""
        val lastName = account.familyName ?: ""
        val email = firebaseUser.email ?: ""

        return User(firstName = firstName, lastName = lastName, email = email)
    }

    private fun saveUserInfo(userID: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userID)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
                runBlocking {
                    _successMessage.send("Your account has been successfully created.")
                }
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }
}
