package com.example.se114_finalproject.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.se114_finalproject.R
import com.example.se114_finalproject.databinding.FragmentProfileBinding
import com.example.se114_finalproject.utilities.showBottomNavigationView
import com.example.se114_finalproject.viewmodel.ProfileVM
import com.example.se114_finalproject.activities.LoginRegister
import com.example.se114_finalproject.utilities.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile){
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
        }

        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                0f,
                emptyArray(),
                false
            )
            findNavController().navigate(action)
        }

        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(), LoginRegister::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.tvVersion.text = "Version 1.0.0"

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.user.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(resource.data!!.imagePath)
                            .error(ColorDrawable(Color.BLACK))
                            .into(binding.imageUser)
                        binding.tvUserName.text = "${resource.data.firstName} ${resource.data.lastName}"
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarSettings.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}