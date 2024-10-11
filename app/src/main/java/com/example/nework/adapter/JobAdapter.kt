package com.example.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nework.R
import com.example.nework.databinding.CardJobBinding
import com.example.nework.dto.Job
import com.example.nework.utils.AndroidUtils


interface Listener {
    fun onEdit(job: Job)
    fun onRemove(job: Job)
}

class JobAdapter(
    private val listener: Listener
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardJobBinding.inflate(inflater, parent, false)
        return JobViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val listener: Listener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        with(binding) {
            jobName.text = job.name
            jobPosition.text = job.position
            jobStart.text = AndroidUtils.formatDate(job.start)
            jobFinish.text = job.finish?.let { AndroidUtils.formatDate(it) } ?: ""
            menu.isVisible = job.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                listener.onRemove(job)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(job)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}