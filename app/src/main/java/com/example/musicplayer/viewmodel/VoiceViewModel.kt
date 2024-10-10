package com.example.musicplayer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.AppConstants
import com.example.musicplayer.data.ChatCompletionRequest
import com.example.musicplayer.data.ChatMessage
import com.example.musicplayer.data.services.ChatCompletionApi
import com.example.musicplayer.data.services.WhisperApi
import com.example.musicplayer.data.services.WhisperApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale
import javax.inject.Inject

data class VoiceUiState(
    val recognizedText: String = "",
    val isRecording: Boolean = false,
    val isAuthenticated: Boolean = false,
    val actionItem: String = ""
)

@HiltViewModel
class VoiceViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val chatCompletionHelper: ChatCompletionHelper,
    private val appleMusicManager: AppleMusicManager
) : ViewModel() {

    private val whisperApi = WhisperApiService.retrofit.create(WhisperApi::class.java)

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthenticated = appleMusicManager.isAuthorized()) }
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            try {
                audioRecorder.startRecording()
                _uiState.update { it.copy(isRecording = true) }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting recording: ${e.message}")
                _uiState.update { it.copy(recognizedText = "Error: ${e.message}") }
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            try {
                audioRecorder.stopRecording()
                _uiState.update { it.copy(isRecording = false) }
                transcribeAudio()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping recording: ${e.message}")
                _uiState.update { it.copy(recognizedText = "Error: ${e.message}") }
            }
        }
    }

    private fun transcribeAudio() {
        val audioFile = audioRecorder.getAudioFile()
        if (audioFile == null || !audioFile.exists()) {
            Log.e(TAG, "Audio file is null or doesn't exist")
            _uiState.update { it.copy(recognizedText = "Error: No audio file available") }
            return
        }

        viewModelScope.launch {
            try {
                val requestFile = audioFile.asRequestBody("audio/mp4".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", audioFile.name, requestFile)
                val modelRequestBody = "whisper-1".toRequestBody("text/plain".toMediaTypeOrNull())

                val response = whisperApi.transcribeAudio("Bearer ${AppConstants.API_KEY}", model = modelRequestBody, body)
                _uiState.update { it.copy(recognizedText = response.text) }
                Log.d(TAG, "Transcription successful: ${response.text}")
                
                // After successful transcription, get action items
                getActionItems(response.text)
            } catch (e: Exception) {
                Log.e(TAG, "Transcription error: ${e.message}", e)
                _uiState.update { it.copy(recognizedText = "Error: ${e.message}") }
            }
        }
    }

    private fun getActionItems(transcribedText: String) {
        viewModelScope.launch {
            val actionItem = chatCompletionHelper.getActionItems(transcribedText)
            _uiState.update { it.copy(actionItem = actionItem) }
            executeActionItem(actionItem)
        }
    }

    private fun executeActionItem(actionItem: String) {
        Log.d(TAG, "actionItem=$actionItem" )

        viewModelScope.launch {
            when (actionItem.lowercase(Locale.getDefault())) {
                "play" -> appleMusicManager.play()
                "pause" -> appleMusicManager.pause()
                "stop" -> appleMusicManager.stop()
                else -> Log.d(TAG, "Unknown action item: $actionItem")
            }
        }
    }

    fun toggleRecording() {
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    fun authenticateAppleMusic() {
        viewModelScope.launch {
            val isAuthenticated = appleMusicManager.authenticate()
            _uiState.update { it.copy(isAuthenticated = isAuthenticated) }
        }
    }

    companion object {
        const val TAG = "VoiceViewModel"
    }
}