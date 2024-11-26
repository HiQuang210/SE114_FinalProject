package com.example.se114_finalproject.fragments.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.se114_finalproject.R
import com.example.se114_finalproject.activities.ShoppingActivity
import com.example.se114_finalproject.databinding.FragmentIntroBinding
import com.example.se114_finalproject.viewmodel.IntroVM
import com.example.se114_finalproject.viewmodel.IntroVM.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.se114_finalproject.viewmodel.IntroVM.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroFragment : Fragment(R.layout.fragment_intro) {
    private lateinit var binding: FragmentIntroBinding
    private val viewModel by viewModels<IntroVM>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroBinding.inflate(inflater)
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.root.startAnimation(fadeInAnimation)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigate.collect { navigationAction ->
                    when (navigationAction) {
                        SHOPPING_ACTIVITY -> {
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                        ACCOUNT_OPTIONS_FRAGMENT -> {
                            findNavController().navigate(navigationAction)
                        }
                        else -> Unit
                    }
                }
            }
        }

        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_accountOptionsFragment)
        }
    }

}