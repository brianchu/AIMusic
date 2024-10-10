package com.example.musicplayer.viewmodel

import android.util.Log
import com.example.musicplayer.data.AppConstants
import com.example.musicplayer.data.ChatCompletionRequest
import com.example.musicplayer.data.ChatMessage
import com.example.musicplayer.data.services.ChatCompletionApi
import com.example.musicplayer.data.services.WhisperApiService
import javax.inject.Inject
import com.example.musicplayer.util.ConfigManager

class ChatCompletionHelper @Inject constructor(
    private val configManager: ConfigManager
) {
    private val openAIApi = WhisperApiService.retrofit.create(ChatCompletionApi::class.java)

    suspend fun getActionItems(transcribedText: String): String {
        try {
            val systemPrompt = configManager.obtainChatCompletionPrompt()

            val request = ChatCompletionRequest(
                model = "gpt-4o",
                messages = listOf(
                    ChatMessage("user", transcribedText),
                    ChatMessage("system", systemPrompt)
                )
            )

            val response = openAIApi.createChatCompletion("Bearer ${AppConstants.API_KEY}", request)
            val actionItem = response.choices.firstOrNull()?.message?.content ?: "No action"

            Log.d(TAG, "Action item received: $actionItem")
            return actionItem
        } catch (e: Exception) {
            Log.e(TAG, "Error getting action items: ${e.message}", e)
            return "Error: ${e.message}"
        }
    }

    companion object {
        const val TAG = "ChatCompletionHelper"
    }
}