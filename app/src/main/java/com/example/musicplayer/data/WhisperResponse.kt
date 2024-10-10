package com.example.musicplayer.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WhisperResponse (
    @Json(name = "text")
    val text: String
)