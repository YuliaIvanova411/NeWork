package ru.netology.nework.ui.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.databinding.DialogSaveCoordsBinding
import com.example.nework.viewmodel.EventViewModel
import com.example.nework.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SaveCoordsDialog : DialogFragment() {

    lateinit var binding: DialogSaveCoordsBinding

    companion object {
        private const val POST = "POST"
        private const val LAT_KEY = "LAT_KEY"
        private const val LONG_KEY = "LONG_KEY"
        fun newInstance(lat: Double, long: Double, post: String?) = SaveCoordsDialog().apply {
            arguments = bundleOf(LAT_KEY to lat, LONG_KEY to long, POST to post)
        }
    }

    private val eventViewModel: EventViewModel by activityViewModels()
    private val postViewModel: PostViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = DialogSaveCoordsBinding.inflate(layoutInflater)
        val lat = requireArguments().getDouble(LAT_KEY).toString().take(9)
        val long = requireArguments().getDouble(LONG_KEY).toString().take(9)
        binding.tvCoords.text = "$lat  ||  $long"

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setNeutralButton(R.string.no) { dialog, _ ->
                dialog.cancel()
                return@setNeutralButton
            }
            .setPositiveButton(R.string.yes) { _, _ ->

                if (requireArguments().getString(POST) != null) {
                    postViewModel.saveCoords(
                        lat.toDouble(),
                        long.toDouble()
                    )
                } else {
                    eventViewModel.saveCoords(
                        lat.toDouble(),
                        long.toDouble()
                    )
                }
                findNavController().navigateUp()
                return@setPositiveButton
            }.create()
    }
}
