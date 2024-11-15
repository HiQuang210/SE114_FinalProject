package com.example.se114_finalproject.fragments.LoginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.se114_finalproject.R
import com.example.se114_finalproject.data.User
import com.example.se114_finalproject.databinding.FragmentRegisterBinding
import com.example.se114_finalproject.utilities.Resource
import com.example.se114_finalproject.viewmodel.RegisterVM
import dagger.hilt.android.AndroidEntryPoint

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterVM>()
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

        binding.apply {
            btnRegReg.setOnClickListener {
                val user = User(
                    edFirstNameReg.text.toString().trim(),
                    edLastNameReg.text.toString().trim(),
                    edEmailReg.text.toString().trim()
                )
                val password = edRegPassword.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.btnRegReg.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test", it.message.toString())
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
    }
}