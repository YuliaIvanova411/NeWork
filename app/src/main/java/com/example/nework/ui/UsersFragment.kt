package com.example.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.nework.databinding.FragmentUsersBinding
import com.example.nework.viewmodel.EventViewModel
import com.example.nework.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import com.example.nework.adapter.OnInteractionListenerUser
import com.example.nework.adapter.UserAdapter
import com.example.nework.dto.User

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UsersFragment : Fragment() {

    lateinit var binding: FragmentUsersBinding

    private val userViewModel: UserViewModel by activityViewModels()
    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUsersBinding.inflate(inflater, container, false)

        val adapter = UserAdapter(object : OnInteractionListenerUser {
            override fun onClick(user: User) {
                if (!user.isSelected) {
                    userViewModel.addSpeaker(user.id)
                    eventViewModel.saveSpeakers(user.id)
                } else {
                    userViewModel.removeSpeaker(user.id)
                    eventViewModel.removeSpeakers(user.id)
                }
            }
        }, 0)


        binding.usersList.adapter = adapter

        lifecycleScope.launchWhenCreated {
            userViewModel.data.collectLatest {
                adapter.submitList(it)
            }
        }

        return binding.root
    }
}