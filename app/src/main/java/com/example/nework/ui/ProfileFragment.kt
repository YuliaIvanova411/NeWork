package com.example.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nework.R
import com.example.nework.auth.AppAuth
import com.example.nework.databinding.FragmentProfileBinding
import com.example.nework.viewmodel.JobViewModel
import com.example.nework.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import com.example.nework.adapter.JobAdapter
import com.example.nework.adapter.OnInteractionListenerJob
import com.example.nework.adapter.ViewPagerAdapter
import com.example.nework.dto.Job
import com.example.nework.utils.loadCircleCrop
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val jobViewModel: JobViewModel by activityViewModels()

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val jobAdapter = JobAdapter(object : OnInteractionListenerJob {
            override fun onEdit(job: Job) {
                jobViewModel.editJob(job)
                val bundle = Bundle().apply {
                    putString("dateStart", job.start)
                    putString("dateFinish", job.finish)
                }
                findNavController().navigate(R.id.action_profileFragment_to_newJobFragment, bundle)
            }

            override fun onRemove(job: Job) {
                jobViewModel.removeById(job.id)
            }
        })

        val userId = arguments?.getInt("userId")
        if (userId != null) {
            jobViewModel.userId.value = userId
            jobViewModel.getJobById(userId)
            userViewModel.getUserById(userId)
            binding.fab.isVisible = appAuth.getId() == userId
        }

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                val urlAvatars = "${user.avatar}"
                avatar.loadCircleCrop(urlAvatars)
                userName.text = user.name
                login.text = user.login
            }
        }

            //  binding.jobList.adapter = jobAdapter

        lifecycleScope.launchWhenCreated {
            jobViewModel.data.collectLatest {
                jobAdapter.submitList(it)
            }
        }

        jobViewModel.stateJob.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        if (userId != null) {
                            jobViewModel.getJobById(userId)
                        }
                    }
                    .show()
            }
            binding.loadJob.isVisible = state.loading
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_newJobFragment)
        }

        binding.listContainer.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.listContainer) { tab, position ->
            val feedList = listOf(getString(R.string.wall), getString(R.string.jobs))
            tab.text = feedList[position]
        }.attach()

        return binding.root
    }
}