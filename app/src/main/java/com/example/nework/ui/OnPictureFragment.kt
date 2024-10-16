package com.example.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nework.databinding.FragmentOnPictureBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.nework.ui.post.NewPostFragment.Companion.textArg
import com.example.nework.utils.load

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class OnPictureFragment : Fragment() {

    lateinit var binding: FragmentOnPictureBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnPictureBinding.inflate(inflater, container, false)

        val urlImages = "${arguments?.textArg}"
        binding.picturePreview.load(urlImages)

        return binding.root
    }
}