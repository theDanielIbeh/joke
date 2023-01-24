package com.example.jokes.network.responses

import com.squareup.moshi.Json

data class JokeResponse(
    @Json(name = "error")
    val error: Boolean,

    @Json(name = "category")
    val category: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "joke")
    val joke: String?,

    @Json(name = "setup")
    val setup: String?,

    @Json(name = "delivery")
    val delivery: String?,

    @Json(name = "flags")
    val flags: Flags,

    @Json(name = "id")
    val id: Int,

    @Json(name = "safe")
    val safe: Boolean,

    @Json(name = "lang")
    val lang: String,
)

data class Flags(
    @Json(name = "nsfw")
    val nsfw: Boolean,

    @Json(name = "religious")
    val religious: Boolean,

    @Json(name = "political")
    val political: Boolean,

    @Json(name = "racist")
    val racist: Boolean,

    @Json(name = "sexist")
    val sexist: Boolean,

    @Json(name = "explicit")
    val explicit: Boolean
)
