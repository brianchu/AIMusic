package com.example.musicplayer.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val chatCompletionPrompt: String by lazy {
        context.assets.open("chat_completion_prompt.txt").bufferedReader().use { it.readText() }
    }

    fun obtainChatCompletionPrompt(): String = chatCompletionPrompt
}
