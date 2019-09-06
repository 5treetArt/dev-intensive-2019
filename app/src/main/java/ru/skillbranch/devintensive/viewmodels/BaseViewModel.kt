package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.repositories.PreferencesRepository

open class BaseViewModel(
    repository: PreferencesRepository = PreferencesRepository
) : ViewModel()  {

    protected val appTheme = MutableLiveData<Int>()

    fun getTheme(): LiveData<Int> = appTheme

    init {
        appTheme.value = repository.getAppTheme()
    }
}
