package com.example.musicplayer.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface MusicManager {
    suspend fun authenticate(): Boolean
    fun isAuthorized(): Boolean
    suspend fun play()
    suspend fun pause()
    suspend fun stop()
}

@Singleton
class SpotifyManager @Inject constructor(private val context: Context) : MusicManager {

    override suspend fun authenticate(): Boolean = withContext(Dispatchers.IO) {
        false
    }

    override fun isAuthorized(): Boolean {
        return false
    }

    override suspend fun play() = withContext(Dispatchers.IO) {
    }

    override suspend fun pause() = withContext(Dispatchers.IO) {
    }

    override suspend fun stop() = withContext(Dispatchers.IO) {
    }

    companion object {
        private const val TAG = "MusicManager"
    }
}