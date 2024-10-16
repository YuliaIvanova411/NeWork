package com.example.nework.ui.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.nework.R
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.FragmentFeedBinding
import com.example.nework.viewmodel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import com.example.nework.adapter.OnInteractionListener
import com.example.nework.adapter.PostAdapter
import com.example.nework.dto.Post
import com.example.nework.ui.dialog.AuthDialog
import com.example.nework.ui.post.NewPostFragment.Companion.textArg
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class FeedFragment : Fragment() {

    lateinit var binding: FragmentFeedBinding
    private val viewModel: PostViewModel by activityViewModels()

    companion object {
        private const val VALUE = "POST"
        private const val OPEN = "open"
        private const val SEE = "see"
    }

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostAdapter(object : OnInteractionListener {

            override fun onLike(post: Post) {
                if (appAuth.getToken() == null) {
                    AuthDialog()
                        .show(childFragmentManager, null)
                } else {
                    if (!post.likedByMe) {
                        viewModel.likeById(post.id)
                    } else {
                        viewModel.unlikeById(post.id)
                    }
                }
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.post_share))
                startActivity(shareIntent)
            }

            override fun onEdit(post: Post) {
                viewModel.editPost(post)
                val bundle = Bundle().apply {
                    putString(OPEN, VALUE)
                }
                findNavController().navigate(
                    R.id.action_nav_feed_to_newPostFragment, bundle
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPicture(post: Post) {
                findNavController().navigate(
                    R.id.action_global_onPictureFragment,
                    Bundle().apply { textArg = post.attachment?.url })
            }

            override fun onCoords(post: Post) {
                val bundle = Bundle().apply {
                    putString(SEE, VALUE)
                    putDouble("lat", post.coords!!.lat)
                    putDouble("long", post.coords.long)
                }
                findNavController().navigate(
                    R.id.mapFragment,
                    bundle
                )
            }

            override fun onProfile(post: Post) {
                if (post.authorJob == null && !post.ownedByMe) {
                    Snackbar.make(binding.root, R.string.empty_jobs, Snackbar.LENGTH_LONG).show()
                } else {
                    val bundle = Bundle().apply {
                        putInt("userId", post.authorId)
                    }
                    findNavController().navigate(R.id.action_global_profileFragment, bundle)
                }
            }
        })

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModel.loadPosts()
                    }
                    .show()
            }
        }

        binding.posts.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefresh.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
            }
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        binding.fab.setOnClickListener {
            if (appAuth.getToken() == null) {
                AuthDialog()
                    .show(parentFragmentManager, null)
            } else {
                val bundle = Bundle().apply {
                    putString(OPEN, VALUE)
                }
                findNavController().navigate(R.id.action_nav_feed_to_newPostFragment, bundle)
            }
        }

        return binding.root
    }
}