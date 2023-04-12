package com.example.jokes.data.joke

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface JokeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(joke: Joke)

    @Delete
    fun delete(joke: Joke)

    @Query("DELETE FROM joke_table WHERE id = :id")
    fun deleteJokeByIdTest(id: Int)

    @Query("SELECT * FROM joke_table WHERE id = :id")
    fun getJokeByIdTest(id: Int): Joke?

    @Query("SELECT * FROM joke_table")
    fun getAllJokesPagingData(): PagingSource<Int, Joke>

    @Query("DELETE FROM joke_table")
    fun clear()
}