package com.example.nework.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.databinding.FragmentRegistrationBinding
import com.example.nework.enumeration.AttachmentType
import com.example.nework.utils.AndroidUtils
import com.example.nework.viewmodel.RegisterViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels()
    private var imageLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.addPhoto(uri, uri.toFile(), AttachmentType.IMAGE)
                    }
                }
            }

        viewModel.media.observe(viewLifecycleOwner) { media ->
            if (media == null) {
                binding.avatarPreview.isGone = true
                return@observe
            } else {
                binding.avatarPreview.isVisible = true
                binding.registerAvatar.setImageURI(media.uri)
            }
        }

        with(binding) {

            registerButton.setOnClickListener {
                val login = signUpLogin.text.toString()
                val password = signUpPassword.text.toString()
                val confirmPassword = confirmPassword.text.toString()
                val name = signUpName.text.toString()

                if (password == confirmPassword) {
                    viewModel.register(login, password, name)
                    AndroidUtils.hideKeyboard(requireView())
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.match_passwords,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            close.setOnClickListener {
                findNavController().navigateUp()
            }
        }


        viewModel.state.observe(viewLifecycleOwner) { state ->

            binding.registerButton.isEnabled = !state.loading

            if (state.loggedIn) {
                findNavController().navigateUp()
            }

            if (state.isBlank) {
                Snackbar.make(
                    binding.root,
                    R.string.blankRegister,
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

        binding.photo.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .compress(2048)
                .createIntent(imageLauncher!!::launch)
        }

        binding.gallery.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .compress(2048)
                .createIntent(imageLauncher!!::launch)
        }

        binding.clear.setOnClickListener {
            viewModel.clearPhoto()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageLauncher = null
    }
}