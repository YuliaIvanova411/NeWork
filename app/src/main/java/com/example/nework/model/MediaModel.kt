package com.example.nework.model

import android.net.Uri
import com.example.nework.dto.AttachmentType
import java.io.File

data class MediaModel(
    val uri: Uri,
    val file: File,
    val attachmentType: AttachmentType? = null,
)