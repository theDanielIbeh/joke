package com.example.jokes.data.joke

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "joke_table")
data class Joke(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "joke")
    val joke: String?,

    @ColumnInfo(name = "setup")
    val setup: String?,

    @ColumnInfo(name = "delivery")
    val delivery: String?,
)