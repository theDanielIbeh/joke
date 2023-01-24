package com.example.jokes.fragments.home

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jokes.network.JokesApi
import com.example.jokes.network.responses.JokeResponse
import com.example.jokes.utils.Constants
import kotlinx.coroutines.launch


class HomeViewModel(private val application: Application) : ViewModel() {

    private val settings: SharedPreferences = application.getSharedPreferences("JokeSettings", 0)

    val categoryType: String? = settings.getString(Constants.CATEGORY_TYPE, "Any")
    var selectedCategoryString: String? = settings.getString(Constants.CATEGORIES, null)
    var selectedFlagString: String? = settings.getString(Constants.FLAGS, null)

    private val _joke = MutableLiveData<JokeResponse?>()
    val joke: MutableLiveData<JokeResponse?>
        get() = _joke

    init {
        getJoke()
    }

    fun getJoke() {
        viewModelScope.launch {
            try {
                val joke =
                    if (selectedCategoryString != null)
                        JokesApi.retrofitService.getJoke(selectedCategoryString!!, selectedFlagString)
                    else
                        categoryType?.let { JokesApi.retrofitService.getJoke(it, selectedFlagString) }
                _joke.value = joke

            } catch (e: Exception) {
                Log.e("Failure: ", "${e.message}")
            }
        }
    }

    class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(application = application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}