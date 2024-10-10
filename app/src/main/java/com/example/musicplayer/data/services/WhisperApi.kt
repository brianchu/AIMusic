package com.example.musicplayer.data.services

import com.example.musicplayer.data.ChatCompletionRequest
import com.example.musicplayer.data.ChatCompletionResponse
import com.example.musicplayer.data.WhisperResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WhisperApi {

    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun transcribeAudio(
        @Header("Authorization") apiKey: String,
        @Part("model") model: RequestBody,
        @Part file: MultipartBody.Part
    ): WhisperResponse
}

interface ChatCompletionApi {

    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatCompletionRequest
    ) : ChatCompletionResponse
}