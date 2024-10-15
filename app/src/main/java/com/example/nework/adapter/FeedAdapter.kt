package com.example.nework.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nework.R
import com.example.nework.databinding.CardItemBinding
import com.example.nework.dto.Event
import com.example.nework.dto.FeedItem
import com.example.nework.view.load
import com.example.nework.enumeration.AttachmentType
import com.example.nework.enumeration.EventType
import com.example.nework.view.loadAttachment

class FeedAdapter(
    private val listener: OnInteractionListener
) : PagingDataAdapter<FeedItem, FeedViewHolder>(FeedDiffCallback()) {

    override fun onBindViewHolder(
        holder: FeedViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                (it as? Payload)?.let { payload ->
                    holder.bind(payload)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardItemBinding.inflate(inflater, parent, false)
        return FeedViewHolder(binding, listener)
    }
}

class FeedViewHolder(
    private val binding: CardItemBinding,
    private val listener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(feedItem: FeedItem) {
        binding.apply {
            feedItem.authorAvatar?.let { avatar.load(it) }
                ?: avatar.setImageResource(R.drawable.no_avatar)
            author.text = feedItem.author
            authorJob.isVisible = feedItem.authorJob != null
            authorJob.text = feedItem.authorJob
            published.text = AndroidUtils.formatDateTime(feedItem.published)
            content.text = feedItem.content

            like.isChecked = feedItem.likedByMe
            like.text = feedItem.likeOwnerIds.size.toString()
            like.setOnClickListener { listener.onLike(feedItem) }

            menu.isVisible = feedItem.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                listener.onRemove(feedItem.id)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(feedItem)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            attachmentImage.visibility = View.GONE
            music.visibility = View.GONE
            attachmentVideo.visibility = View.GONE
            if (feedItem.attachment != null) {
                when (feedItem.attachment?.type) {
                    AttachmentType.AUDIO -> {
                        music.visibility = View.VISIBLE
                        playButton.setOnClickListener { listener.onPlayPause(feedItem) }
                        musicTitle.text = feedItem.attachment?.url
                    }
                    AttachmentType.IMAGE -> {
                        attachmentImage.visibility = View.VISIBLE
                        feedItem.attachment?.url?.let { url ->
                            attachmentImage.loadAttachment(url)
                            attachmentImage.setOnClickListener { listener.onImage(url) }
                        }

                    }
                    AttachmentType.VIDEO -> {
                        attachmentVideo.visibility = View.VISIBLE
                        feedItem.attachment?.url?.let { url ->
                            attachmentVideo.setOnClickListener { listener.onVideo(url) }
                            val uri = Uri.parse(url)
                            attachmentVideo.setVideoURI(uri)
                            attachmentVideo.setOnPreparedListener { mp ->
                                mp?.setVolume(0F, 0F)
                                mp?.isLooping = true
                                attachmentVideo.start()
                            }
                        }
                    }
                    else -> Unit
                }
            }

            coordinates.isVisible = feedItem.coords != null
            feedItem.coords?.let { coords ->
                coordinates.setOnClickListener {
                    listener.onCoordinates(
                        coords.lat.toDouble(),
                        coords.lat.toDouble()
                    )
                }
            }

            link.isVisible = feedItem.link != null
            link.text = feedItem.link

            if (feedItem is Event) {
                eventGroup.visibility = View.VISIBLE
                eventDate.text = AndroidUtils.formatDateTime(feedItem.datetime)
                eventType.text = feedItem.type.toString()
                when (feedItem.type) {
                    EventType.ONLINE -> typeOfEventIcon.setImageResource(R.drawable.online_event)
                    EventType.OFFLINE -> typeOfEventIcon.setImageResource(R.drawable.offline_event)
                }
            } else {
                eventGroup.visibility = View.GONE
            }

            avatar.setOnClickListener {
                listener.onUser(feedItem.authorId)
            }
        }

    }

    fun bind(payload: Payload) {
        payload.likeByMe?.let {
            binding.like.isChecked = it
        }

        payload.content?.let {
            binding.content.text = it
        }
    }
}

data class Payload(
    val likeByMe: Boolean? = null,
    val content: String? = null,
)


class FeedDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}

interface OnInteractionListener {
    fun onLike(feedItem: FeedItem)
    fun onRemove(id: Int)
    fun onEdit(feedItem: FeedItem)
    fun onUser(userId: Int)
    fun onPlayPause(feedItem: FeedItem)
    fun onCoordinates(lat: Double, long: Double)
    fun onVideo(url: String)
    fun onImage(url: String)
}
