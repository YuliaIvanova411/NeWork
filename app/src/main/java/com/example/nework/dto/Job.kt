package com.example.nework.dto

data class Job (
    val id: Int,
    val name: String,
    val position: String,
    val start: String,
    val finish: String? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
)
