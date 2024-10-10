package com.example.musicplayer.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorder @Inject constructor(@ApplicationContext private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var currentAudioFile: File? = null
    private var isRecording = false

    suspend fun startRecording() = withContext(Dispatchers.IO) {
        if (isRecording) return@withContext

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        currentAudioFile = File(context.cacheDir, "audio_record_$timestamp.mp4")
        
        Log.d("AudioRecorder", "Creating new audio file: ${currentAudioFile?.absolutePath}")

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        try {
            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(currentAudioFile?.absolutePath)
                
                prepare()
                start()
                isRecording = true
                Log.d("AudioRecorder", "Recording started")
            }
        } catch (e: IOException) {
            Log.e("AudioRecorder", "prepare() failed: ${e.message}")
            isRecording = false
            throw e
        } catch (e: IllegalStateException) {
            Log.e("AudioRecorder", "setAudioSource failed: ${e.message}")
            isRecording = false
            throw e
        }
    }

    suspend fun stopRecording() = withContext(Dispatchers.IO) {
        if (!isRecording) return@withContext

        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
            Log.d("AudioRecorder", "Recording stopped. File: ${currentAudioFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error stopping recording: ${e.message}")
        }
    }

    fun getAudioFile(): File? {
        Log.d("AudioRecorder", "Getting audio file: ${currentAudioFile?.absolutePath}")
        return currentAudioFile
    }
}