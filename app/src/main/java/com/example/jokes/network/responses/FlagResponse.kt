package com.example.jokes.network.responses

import com.squareup.moshi.Json

data class FlagResponse(
    @Json(name = "error")
    val error: Boolean,

    @Json(name = "flags")
    val flags: List<String>,

    @Json(name = "timestamp")
    val timestamp: Long
)
