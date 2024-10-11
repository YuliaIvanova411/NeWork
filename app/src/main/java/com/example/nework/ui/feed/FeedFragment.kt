package com.example.nework.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.adapter.ViewPagerAdapter
import com.example.nework.databinding.FragmentFeedBinding
import com.example.nework.ui.profile.UserFragment
import com.example.nework.view.load
import com.example.nework.viewmodel.AuthViewModel
import com.example.nework.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.community.dialog.SignOutDialog


@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        authViewModel.data.observe(viewLifecycleOwner) { authModel ->
            binding.apply {
                if (authViewModel.authorized) {
                    menuAuth.visibility = View.INVISIBLE
                    ownerGroup.visibility = View.VISIBLE
                    logOut.visibility = View.VISIBLE
                } else {
                    menuAuth.visibility = View.VISIBLE
                    ownerGroup.visibility = View.INVISIBLE
                    logOut.visibility = View.INVISIBLE
                }
            }

            userViewModel.getUserById(authModel.id)
        }

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.ownerName.text = user.name
            user.avatar?.apply {
                binding.ownerAvatar.load(this)
            } ?: binding.ownerAvatar.setImageResource(R.drawable.no_avatar)
            binding.ownerAvatar.setOnClickListener {
                findNavController().navigate(
                    R.id.action_feedFragment_to_userFragment,
                    bundleOf(UserFragment.USER_ID to user.id)
                )
            }
        }

        binding.menuAuth.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.menu_auth)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.login -> {
                            findNavController().navigate(R.id.loginFragment)
                            true
                        }
                        R.id.register -> {
                            findNavController().navigate(R.id.registerFragment)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        binding.logOut.setOnClickListener {
            SignOutDialog(object : SignOutDialog.ConfirmationListener {
                override fun confirmButtonClicked() {
                    authViewModel.logout()
                    findNavController().navigate(R.id.action_feedFragment_to_greetingFragment)
                }
            }).show(childFragmentManager, SignOutDialog.TAG)
        }

        // TabLayout + ViewPager2
        binding.listContainer.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.listContainer) { tab, position ->
            val feedList = listOf(getString(R.string.posts), getString(R.string.events))
            tab.text = feedList[position]
        }.attach()

        binding.addButton.setOnClickListener {
            if (!authViewModel.authorized) {
                findNavController().navigate(R.id.action_feedFragment_to_signInDialog)
            } else {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_add)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.new_post -> {
                                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                                true
                            }
                            R.id.new_event -> {
                                findNavController().navigate(R.id.action_feedFragment_to_newEventFragment)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }

        return binding.root
    }
}