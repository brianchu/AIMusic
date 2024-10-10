package com.example.musicplayer.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    val choices: List<Choice>
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class Choice(
    val message: ChatMessage
)