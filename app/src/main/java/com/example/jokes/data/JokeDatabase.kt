package com.example.jokes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jokes.data.joke.Joke
import com.example.jokes.data.joke.JokeDao

@Database(entities = [Joke::class], version = 1, exportSchema = false)
abstract class JokeDatabase : RoomDatabase() {
    abstract val jokeDao: JokeDao

    companion object {
        @Volatile
        private var INSTANCE: JokeDatabase? = null

        fun getInstance(context: Context): JokeDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        JokeDatabase::class.java,
                        "joke_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}