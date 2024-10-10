package com.example.nework.entity.wall

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nework.dto.Attachment
import com.example.nework.dto.Coordinates
import com.example.nework.dto.Post
import com.example.nework.dto.UserPreview


@Entity
data class WallEntity(
    @PrimaryKey
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coords: Coordinates?,
    val link: String? = null,
    val likeOwnerIds: List<Int> = emptyList(),
    val mentionIds: List<Int> = emptyList(),
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded
    val attachment: Attachment? = null,
    val ownedByMe: Boolean,
    val users: Map<Int, UserPreview> = emptyMap(),
) {
    fun toDto() = Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        published = published,
        coords = coords,
        link = link,
        likeOwnerIds = likeOwnerIds,
        mentionIds = mentionIds,
        mentionedMe = mentionedMe,
        likedByMe = likedByMe,
        attachment = attachment,
        ownedByMe = ownedByMe,
        users = users,
    )

    companion object {
        fun fromDto(post: Post) = WallEntity(
            id = post.id,
            authorId = post.authorId,
            author = post.author,
            authorAvatar = post.authorAvatar,
            authorJob = post.authorJob,
            content = post.content,
            published = post.published,
            coords = post.coords,
            link = post.link,
            likeOwnerIds = post.likeOwnerIds,
            mentionIds = post.mentionIds,
            mentionedMe = post.mentionedMe,
            likedByMe = post.likedByMe,
            attachment = post.attachment,
            ownedByMe = post.ownedByMe,
            users = post.users,
        )
    }
}

fun List<Post>.toEntity(): List<WallEntity> = map(WallEntity.Companion::fromDto)