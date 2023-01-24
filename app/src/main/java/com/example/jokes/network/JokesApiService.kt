package com.example.jokes.network

import com.example.jokes.network.responses.CategoryResponse
import com.example.jokes.network.responses.FlagResponse
import com.example.jokes.network.responses.JokeResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://v2.jokeapi.dev"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit =
    Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

interface JokesApiService {

    @GET("joke/{categories}")
    suspend fun getJoke(
        @Path("categories")
        categories: String,

        @Query("blacklistFlags")
        blacklistFlags: String?
    ): JokeResponse

    @GET("flags")
    suspend fun getFlags(): FlagResponse

    @GET("categories")
    suspend fun getCategories(): CategoryResponse
}

object JokesApi {
    val retrofitService: JokesApiService by lazy {
        retrofit.create(JokesApiService::class.java)
    }
}