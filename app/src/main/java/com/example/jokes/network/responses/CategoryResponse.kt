package com.example.jokes.network.responses

import com.squareup.moshi.Json

data class CategoryResponse(
    @Json(name = "error")
    val error: Boolean,

    @Json(name = "categories")
    val categories: List<String>,

    @Json(name = "categoryAliases")
    val categoryAliases: List<CategoryAlias>,

    @Json(name = "timestamp")
    val timestamp: Long
)

data class CategoryAlias(
    @Json(name = "alias")
    val alias: String,

    @Json(name = "resolved")
    val resolved: String
)
