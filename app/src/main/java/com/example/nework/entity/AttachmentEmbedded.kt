package com.example.nework.entity

import com.example.nework.dto.Attachment
import com.example.nework.dto.AttachmentType


data class AttachmentEmbedded(
    val url: String,
    val type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbedded(it.url, it.type)
        }
    }
}