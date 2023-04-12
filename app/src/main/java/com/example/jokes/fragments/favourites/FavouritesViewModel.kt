package com.example.jokes.fragments.favourites

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.jokes.data.joke.JokeRepository


class FavouritesViewModel(private val application: Application) : ViewModel() {
    private val jokeRepository: JokeRepository = JokeRepository(application.applicationContext)
    val jokesData = jokeRepository.getAllJokesPagingData(10).cachedIn(viewModelScope)

    class FavouritesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
                return FavouritesViewModel(application = application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}