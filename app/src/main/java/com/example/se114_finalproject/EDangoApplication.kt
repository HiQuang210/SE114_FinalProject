package com.example.se114_finalproject

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@Suppress("DEPRECATION")
@HiltAndroidApp
class EDangoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.setApplicationId("591392283307732")
        FacebookSdk.setClientToken("1aaa528ebbec71f52b15f776bf01aedf")
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}