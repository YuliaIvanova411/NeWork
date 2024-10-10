package com.example.nework.view

import android.widget.ImageView
import com.example.nework.R
import com.bumptech.glide.Glide

fun ImageView.load(url: String) =
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.loading_100dp)
        .error(R.drawable.error_100dp)
        .timeout(10_000)
        .into(this)

fun ImageView.loadAttachment(url: String) =
    Glide.with(this)
    .load(url)
    .timeout(10_000)
    .into(this)