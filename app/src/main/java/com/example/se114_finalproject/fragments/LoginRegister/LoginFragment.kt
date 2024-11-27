@file:Suppress("DEPRECATION")

package com.example.se114_finalproject.fragments.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.se114_finalproject.R
import com.example.se114_finalproject.activities.ShoppingActivity
import com.example.se114_finalproject.databinding.FragmentLoginBinding
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.viewmodel.LoginVM
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginVM>()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val googleSignInRequestCode = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGoogleSignIn()

        binding.tvLoginSubtitle2.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.tvForgotPass.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
        }

        binding.btnLoginLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.edLoginEmail.error = "Email cannot be empty"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.edLoginPassword.error = "Password cannot be empty"
                return@setOnClickListener
            }

            viewModel.loginWithEmailAndPassword(email, password)
        }

        binding.gmailLoginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, googleSignInRequestCode)
        }

        binding.chkShowPass.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.edLoginPassword.transformationMethod = null
            } else {
                binding.edLoginPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            }
            binding.edLoginPassword.setSelection(binding.edLoginPassword.text?.length ?: 0)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.login.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.btnLoginLogin.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.btnLoginLogin.revertAnimation()
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                            binding.btnLoginLogin.revertAnimation()
                        }
                        else -> Unit
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == googleSignInRequestCode) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                account?.let {
                    handleGoogleSignInResult(it)
                }
            } else {
                Log.e("LoginFragment", "Google sign-in failed")
            }
        }
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        viewModel.loginWithGoogle(credential)
    }
}
