package com.example.jokes.data.joke

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.jokes.data.JokeDatabase

class JokeRepository(context: Context) {
    private val database= JokeDatabase.getInstance(context)

    suspend fun insert(joke: Joke) {
        database.jokeDao.insert(joke)
    }

    fun getAllJokesPagingData(
        pageSize: Int
    ): LiveData<PagingData<Joke>> = Pager(
        config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
        pagingSourceFactory = {
            database.jokeDao.getAllJokesPagingData()
        }
    ).liveData
}