package com.example.nework.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.nework.R
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.FragmentMyWallBinding
import com.example.nework.viewmodel.MyWallViewModel
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
class MyWallFragment : Fragment() {

    lateinit var binding: FragmentMyWallBinding

    private val viewModel: PostViewModel by activityViewModels()
    private val myWallViewModel: MyWallViewModel by activityViewModels()

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

        binding = FragmentMyWallBinding.inflate(inflater, container, false)

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
                    R.id.action_nav_my_wall_to_newPostFragment, bundle
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
                val bundle = Bundle().apply {
                    putInt("userId", post.authorId)
                }
                findNavController().navigate(R.id.action_global_profileFragment, bundle)
            }
        })

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.myPosts.smoothScrollToPosition(0)
                }
            }
        })

        binding.myPosts.adapter = adapter

        lifecycleScope.launchWhenCreated {
            myWallViewModel.data(appAuth.getId()).collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefresh.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
                binding.emptyPostTitle.isVisible =
                    adapter.itemCount < 1
            }
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        myWallViewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        myWallViewModel.loadPostsById(appAuth.getId())
                    }
                    .show()
            }
        }

        if (appAuth.getToken() == null) {
            binding.loginTitle.isVisible = true
            binding.loginButton.isVisible = true
            binding.fab.isVisible = false
            binding.myPosts.isVisible = false
        } else {
            binding.loginTitle.isVisible = false
            binding.loginButton.isVisible = false
            binding.fab.isVisible = true
            binding.myPosts.isVisible = true
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_nav_my_wall_to_newPostFragment)
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_global_signInFragment)
        }

        return binding.root
    }
}