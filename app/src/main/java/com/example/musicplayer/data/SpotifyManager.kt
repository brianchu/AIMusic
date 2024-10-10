package com.example.musicplayer.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyManager @Inject constructor(private val context: Context) {

    suspend fun authenticate(): Unit = withContext(Dispatchers.IO) {
    }

    fun isAuthorized(): Boolean {
        return false
    }

    suspend fun play() = withContext(Dispatchers.IO) {
    }

    suspend fun pause() = withContext(Dispatchers.IO) {
    }

    suspend fun stop() = withContext(Dispatchers.IO) {
    }

    companion object {
        private const val TAG = "MusicManager"
    }
}