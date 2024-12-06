@file:Suppress("DEPRECATION")

package com.example.se114_finalproject.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.se114_finalproject.R
import com.example.se114_finalproject.data.User
import com.example.se114_finalproject.databinding.FragmentRegisterBinding
import com.example.se114_finalproject.utilities.RegisterValidation
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.viewmodel.RegisterVM
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "RegisterFragment"
private const val GOOGLE_SIGN_IN_REQUEST_CODE = 1001

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterVM>()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGoogleSignIn()
        setupFacebookSignIn()

        binding.gmailRegButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
        }

        binding.facebookRegButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )
        }

        binding.tvRegSubtitle2.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            btnRegReg.setOnClickListener {
                val user = User(
                    edFirstNameReg.text.toString().trim(),
                    edLastNameReg.text.toString().trim(),
                    edEmailReg.text.toString().trim()
                )
                val password = edRegPassword.text.toString()
                val confirmPassword = edRegConfirmPassword.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password, confirmPassword)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.register.collect {
                        when (it) {
                            is Resource.Loading -> {
                                binding.btnRegReg.startAnimation()
                            }
                            is Resource.Success -> {
                                Log.d(TAG, it.message.toString())
                                binding.btnRegReg.revertAnimation()
                            }
                            is Resource.Error -> {
                                Log.e(TAG, it.message.toString())
                                binding.btnRegReg.revertAnimation()
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.googleSignInState.collect { resource ->
                        when (resource) {
                            is Resource.Loading -> Log.d(TAG, "Signing in with Google...")
                            is Resource.Success -> {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Success")
                                    .setMessage("Welcome, ${resource.data?.firstName}!")
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                            is Resource.Error -> {
                                Log.e(TAG, resource.message.toString())
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.facebookSignInState.collect { resource ->
                        when (resource) {
                            is Resource.Loading -> Log.d(TAG, "Signing in with Facebook...")
                            is Resource.Success -> {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Success")
                                    .setMessage("Welcome, ${resource.data?.firstName}!")
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                            is Resource.Error -> {
                                Log.e(TAG, resource.message.toString())
                            }
                            else -> Unit
                        }
                    }
                }

                launch {
                    viewModel.validation.collect { validation ->
                        if (validation.firstName is RegisterValidation.Failed) {
                            withContext(Dispatchers.Main) {
                                binding.edFirstNameReg.apply {
                                    requestFocus()
                                    error = validation.firstName.message
                                }
                            }
                        }

                        if (validation.lastName is RegisterValidation.Failed) {
                            withContext(Dispatchers.Main) {
                                binding.edLastNameReg.apply {
                                    requestFocus()
                                    error = validation.lastName.message
                                }
                            }
                        }

                        if (validation.email is RegisterValidation.Failed) {
                            withContext(Dispatchers.Main) {
                                binding.edEmailReg.apply {
                                    requestFocus()
                                    error = validation.email.message
                                }
                            }
                        }

                        if (validation.password is RegisterValidation.Failed) {
                            withContext(Dispatchers.Main) {
                                binding.edRegPassword.apply {
                                    requestFocus()
                                    error = validation.password.message
                                }
                            }
                        }

                        if (validation.confirmPassword is RegisterValidation.Failed) {
                            withContext(Dispatchers.Main) {
                                binding.edRegConfirmPassword.apply {
                                    requestFocus()
                                    error = validation.confirmPassword.message
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.successMessage.collect { message ->
                        showSuccessDialog(message)
                        resetInputFields()
                    }
                }
            }
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookSignInResult(result.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "Facebook login canceled")
                }

                override fun onError(error: FacebookException) {
                    Log.e(TAG, "Facebook login failed: ${error.message}")
                }
            }
        )
    }

    private fun handleFacebookSignInResult(accessToken: AccessToken) {
        val credential = com.google.firebase.auth.FacebookAuthProvider.getCredential(accessToken.token)
        viewModel.signInWithFacebook(credential)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                account?.let {
                    handleGoogleSignInResult(it)
                }
            } else {
                Log.e(TAG, "Google sign-in failed: ${task.exception?.message}")
            }
        }
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        viewModel.signInWithGoogle(credential, account)
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun resetInputFields() {
        with(binding) {
            edFirstNameReg.text?.clear()
            edLastNameReg.text?.clear()
            edEmailReg.text?.clear()
            edRegPassword.text?.clear()
            edRegConfirmPassword.text?.clear()
        }
    }
}
