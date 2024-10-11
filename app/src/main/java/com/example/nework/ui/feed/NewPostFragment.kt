package com.example.nework.ui.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.databinding.FragmentNewPostBinding
import com.example.nework.enumeration.AttachmentType
import com.example.nework.ui.map.MapFragment
import com.example.nework.utils.AndroidUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.example.nework.viewmodel.PostViewModel
import com.google.android.material.snackbar.Snackbar




class NewPostFragment: Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    private var imageLauncher: ActivityResultLauncher<Intent>? = null

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.clearEdited()
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.eventGroup.isVisible = false

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        viewModel.getEditPost()?.let { post ->
            binding.editNewPost.setText(post.content)
            binding.editTextLink.setText(post.link)
            if (post.attachment?.type == AttachmentType.IMAGE) {
                binding.textViewImage.text = post.attachment.url
            }
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            binding.textViewLat.text = it.coords?.lat ?: ""
            binding.textViewLong.text = it.coords?.long ?: ""
            it.attachment.let { attachment ->
                binding.textViewImage.text =
                    if (attachment?.type == AttachmentType.IMAGE) attachment.url
                    else ""
            }
        }

        binding.createButton.setOnClickListener {
            val content = binding.editNewPost.text.toString()
            val link = AndroidUtils.checkLink(binding.editTextLink.text.toString())
            if (link == "") {
                Snackbar.make(binding.root, R.string.invalid_link, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.changeContent(content, link)
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.back.setOnClickListener {
            viewModel.clearEdited()
            findNavController().navigateUp()
        }


        // Attachment Image
        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.addMedia(uri, uri.toFile(), AttachmentType.IMAGE)
                    }
                }
            }
        binding.addImageButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .createIntent(imageLauncher!!::launch)
        }
        binding.clearImage.setOnClickListener {
            viewModel.clearMedia()
        }
        viewModel.media.observe(viewLifecycleOwner) { media ->
            media?.let {
                binding.textViewImage.text = media.file.name
            }
        }


        // Coordinates
        binding.addPlaceButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_newPostFragment_to_mapFragment,
                bundleOf(
                    MapFragment.ITEM_TYPE to MapFragment.Companion.ItemType.POST.name
                )
            )
        }
        binding.clearCoordinates.setOnClickListener {
            viewModel.clearCoordinates()
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageLauncher = null
    }
}