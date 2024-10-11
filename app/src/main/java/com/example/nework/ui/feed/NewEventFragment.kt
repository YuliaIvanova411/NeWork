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
import com.example.nework.enumeration.EventType
import com.example.nework.feed.DatePickerFragment
import com.example.nework.feed.TimePickerFragment
import com.example.nework.ui.map.MapFragment
import com.example.nework.utils.AndroidUtils
import com.example.nework.viewmodel.EventViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import java.util.*

class NewEventFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
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
        binding.eventGroup.isVisible = true

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)

        val event = viewModel.getEditEvent()
        event?.let {
            binding.editNewPost.setText(it.content)
            binding.textViewDate.text = AndroidUtils.formatDate(it.datetime)
            binding.textViewTime.text = AndroidUtils.formatTime(it.datetime)
            if (it.type == EventType.OFFLINE) {
                binding.typeOffline.isChecked = true
            } else {
                binding.typeOnline.isChecked = true
            }
            binding.editTextLink.setText(it.link)
            if (it.attachment?.type == AttachmentType.IMAGE) {
                binding.textViewImage.text = it.attachment.url
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
            val datetime =
                if (binding.textViewDate.text.isNotBlank() && binding.textViewTime.text.isNotBlank()) {
                    binding.textViewDate.text.toString() + "T" + binding.textViewTime.text.toString() + ":00.000000Z"
                } else event?.datetime ?: ""
            if (datetime.isBlank()) {
                Snackbar.make(
                    binding.root,
                    R.string.empty_datetime,
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val eventType =
                if (binding.typeOffline.isChecked) {
                    EventType.OFFLINE
                } else {
                    EventType.ONLINE
                }
            val link = AndroidUtils.checkLink(binding.editTextLink.text.toString())
            if (link == "") {
                Snackbar.make(binding.root, R.string.invalid_link, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.changeContent(
                content = content,
                datetime = datetime,
                eventType = eventType,
                link = link,
            )
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.back.setOnClickListener {
            viewModel.clearEdited()
            findNavController().navigateUp()
        }


        // Datetime
        binding.addDate.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = AndroidUtils.formatDatePicker(selectedDate.time)
                    binding.textViewDate.text = date
                }
            datePickerFragment.show(childFragmentManager, "datePicker")

        }
        binding.addTime.setOnClickListener {
            val timePickerFragment =
                TimePickerFragment { hour, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hour)
                    selectedTime.set(Calendar.MINUTE, minute)
                    val time = AndroidUtils.formatTimePicker(selectedTime.time)
                    binding.textViewTime.text = time
                }
            timePickerFragment.show(childFragmentManager, "timePicker")
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
                R.id.action_newEventFragment_to_mapFragment,
                bundleOf(
                    MapFragment.ITEM_TYPE to MapFragment.Companion.ItemType.EVENT.name
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