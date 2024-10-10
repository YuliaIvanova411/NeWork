package com.example.nework.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.example.nework.databinding.FragmentLoginBinding
import com.example.nework.viewmodel.AuthViewModel


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        with(binding) {
            loginButton.setOnClickListener {
                val login = signUpLogin.text.toString()
                val password = signUpPassword.text.toString()
                viewModel.login(login, password)
            }

            close.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            binding.loginButton.isEnabled = !state.loading

            if (state.loggedIn) {
                findNavController().navigateUp()
            }

            if (state.isBlank) {
                Snackbar.make(
                    binding.root,
                    R.string.blankAuth,
                    Snackbar.LENGTH_LONG
                ).show()
            }

            if (state.invalidLoginOrPass) {
                Snackbar.make(
                    binding.root,
                    R.string.invalid_auth,
                    Snackbar.LENGTH_LONG
                ).show()
            }

            if (state.error) {
                Snackbar.make(
                    binding.root,
                    R.string.error_loading,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        return binding.root
    }
}