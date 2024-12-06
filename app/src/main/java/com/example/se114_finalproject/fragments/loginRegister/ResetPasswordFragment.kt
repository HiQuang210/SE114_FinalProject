package com.example.se114_finalproject.fragments.loginRegister

import android.os.Bundle
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
import com.example.se114_finalproject.databinding.FragmentResetpassBinding
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.viewmodel.ResetPasswordVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : Fragment(R.layout.fragment_resetpass) {
    private lateinit var binding: FragmentResetpassBinding
    private val resetPasswordVM: ResetPasswordVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetpassBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnResetPassword.setOnClickListener {
                val email = edResetEmail.text.toString().trim()
                if (email.isNotEmpty()) {
                    resetPasswordVM.sendResetPasswordEmail(email)
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            }
            btnResetBack.setOnClickListener{
                findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                resetPasswordVM.resetPassword.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            // Loading indicator
                        }
                        is Resource.Success -> {
                            binding.edResetEmail.isEnabled = false
                            binding.btnResetPassword.isEnabled = false
                            Toast.makeText(
                                requireContext(),
                                "Reset password instruction sent, please check your email",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                resource.message ?: "An error occurred",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
