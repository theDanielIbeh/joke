package com.example.jokes.fragments.settings

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.example.jokes.network.JokesApi
import com.example.jokes.utils.Constants
import kotlinx.coroutines.launch


class SettingsViewModel(private val application: Application) : ViewModel() {

    val settings: SharedPreferences = application.getSharedPreferences("JokeSettings", 0)
    val editor: SharedPreferences.Editor = settings.edit()

    var selectedCategoryList = arrayListOf<String>()
    var selectedCategoryString: String? = settings.getString(Constants.CATEGORIES, null)
    var unselectedCategoryList = arrayListOf<String>()

    var selectedFlagList = arrayListOf<String>()
    var selectedFlagString: String? = settings.getString(Constants.FLAGS, null)
    var unselectedFlagList = arrayListOf<String>()
    var filters = Filters()

    private val _categoryList = MutableLiveData<List<String>>()
    val categoryList: LiveData<List<String>>
        get() = _categoryList

    private val _flagList = MutableLiveData<List<String>>()
    val flagList: LiveData<List<String>>
        get() = _flagList

//    private val _joke = MutableLiveData<JokeResponse>()
//    val joke: LiveData<JokeResponse>
//        get() = _joke

    private val _filtersLive = MutableLiveData<Filters>()
    val filtersLive: LiveData<Filters>
        get() = _filtersLive

    val liveDataMerger: MediatorLiveData<List<String>> = MediatorLiveData<List<String>>()

    init {
        getJokeCategories()
    }

    fun updateFiltersLive() {
        _filtersLive.value = filters
    }

    private fun getJokeCategories() {
        viewModelScope.launch {
            try {
                val categories =
                    JokesApi.retrofitService.getCategories()
                _categoryList.value = categories.categories

                val flagList =
                    JokesApi.retrofitService.getFlags()
                _flagList.value = flagList.flags

                liveDataMerger.addSource(_categoryList) { value->
                    liveDataMerger.setValue(
                        value
                    )
                }
                liveDataMerger.addSource(_flagList) { value ->
                    liveDataMerger.setValue(
                        value
                    )
                }
//                val joke =
//                    JokesApi.retrofitService.getJoke(selectedCategoryString, null)
//                _joke.value = joke

            } catch (e: Exception) {
                Log.e("Failure: ", "${e.message}")
            }
        }
    }

    class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(application = application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}