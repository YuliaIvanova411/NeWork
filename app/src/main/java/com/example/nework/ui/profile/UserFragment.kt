package com.example.nework.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.nework.R
import com.example.nework.databinding.FragmentUserBinding
import com.example.nework.dto.FeedItem
import com.example.nework.dto.Post
import com.example.nework.enumeration.AttachmentType
import com.example.nework.ui.MediaLifecycleObserver
import com.example.nework.ui.map.MapFragment
import com.example.nework.view.load
import com.example.nework.viewmodel.JobViewModel
import com.example.nework.viewmodel.PostViewModel
import com.example.nework.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class UserFragment : Fragment() {

    private val userViewModel by activityViewModels<UserViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val jobViewModel by activityViewModels<JobViewModel>()

    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserBinding.inflate(inflater, container, false)

        val userId = requireArguments().getInt(USER_ID)

        userViewModel.getUserById(userId)
        jobViewModel.getJobsByUserId(userId)

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.titleName.text = user.name
            user.avatar?.let { binding.userAvatar.load(it) }
                ?: binding.userAvatar.setImageResource(R.drawable.no_avatar)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                jobViewModel.data.collectLatest { jobsList ->
                    if (jobsList.isNotEmpty()) {
                        val job = jobViewModel.getCurrentJob(jobsList)
                        binding.titlePosition.text = job.position
                        binding.titleWork.text = job.name
                    }
                }
            }
        }

        binding.iconJob.setOnClickListener {
            findNavController().navigate(
                R.id.action_userFragment_to_jobsFragment,
                bundleOf(JobsFragment.USER_ID to userId)
            )
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        val adapter = FeedAdapter(object : OnInteractionListener {
            override fun onLike(feedItem: FeedItem) {
                postViewModel.likeById(feedItem as Post)
            }

            override fun onRemove(id: Int) {
                postViewModel.wallRemoveById(id)
            }

            override fun onEdit(feedItem: FeedItem) {
                findNavController().navigate(R.id.action_userFragment_to_newPostFragment)
                postViewModel.edit(feedItem as Post)
            }

            override fun onUser(userId: Int) = Unit

            override fun onPlayPause(feedItem: FeedItem) {
                if (feedItem.attachment?.type == AttachmentType.AUDIO) {
                    feedItem.attachment?.url?.let { mediaObserver.playPause(it) }
                }
            }

            override fun onCoordinates(lat: Double, long: Double) {
                findNavController().navigate(
                    R.id.action_userFragment_to_mapFragment,
                    bundleOf(
                        MapFragment.LAT_KEY to lat,
                        MapFragment.LONG_KEY to long
                    )
                )
            }

            override fun onVideo(url: String) {
                findNavController().navigate(
                    R.id.action_userFragment_to_videoFragment,
                    bundleOf(
                        VideoFragment.URL to url
                    )
                )
            }

            override fun onImage(url: String) {
                findNavController().navigate(
                    R.id.action_userFragment_to_imageFragment,
                    bundleOf(
                        ImageFragment.URL to url
                    )
                )
            }
        })
        binding.listContainer.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.wallData(userId).collectLatest { wall ->
                    adapter.submitData(wall)
                }
            }
        }

        userViewModel.userDataState.observe(viewLifecycleOwner) { state ->
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swiperefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)

        mediaObserver.player?.setOnCompletionListener {
            mediaObserver.player?.stop()
        }

        return binding.root
    }

    companion object {
        const val USER_ID = "USER_ID"
    }
}