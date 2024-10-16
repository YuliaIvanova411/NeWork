package com.example.nework.application

import android.app.Application
import com.github.dhaval2404.imagepicker.BuildConfig
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NeWorkApp: Application () {
    override fun onCreate() {
        MapKitFactory.setApiKey(BuildConfig.MAPS_API_KEY)
        super.onCreate()
    }
}