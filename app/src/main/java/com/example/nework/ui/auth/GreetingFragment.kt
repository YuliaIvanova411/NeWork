package com.example.nework.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.databinding.FragmentGreetingBinding
import com.example.nework.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GreetingFragment : Fragment() {

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGreetingBinding.inflate(inflater, container, false)
        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized) {
                findNavController().navigate(R.id.action_greetingFragment_to_feedFragment)
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_greetingFragment_to_registerFragment)
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_greetingFragment_to_loginFragment)
        }

        binding.close.setOnClickListener {
            findNavController().navigate(R.id.action_greetingFragment_to_feedFragment)
        }

        return binding.root
    }
}