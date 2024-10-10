package com.example.musicplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeFilePath()
    }

    private fun initializeFilePath() {
    }
}