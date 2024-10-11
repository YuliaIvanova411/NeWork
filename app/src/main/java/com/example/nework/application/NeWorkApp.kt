package com.example.nework.application

import android.app.Application
import com.github.dhaval2404.imagepicker.BuildConfig
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CommunityApp: Application() {
    private val mapkitApiKey = BuildConfig.API_KEY

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(mapkitApiKey)
    }
}