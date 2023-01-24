package com.example.jokes

import android.app.Application
import com.example.jokes.data.JokeDatabase

class JokeApplication: Application() {
    val database: JokeDatabase by lazy {JokeDatabase.getInstance(this)}
}