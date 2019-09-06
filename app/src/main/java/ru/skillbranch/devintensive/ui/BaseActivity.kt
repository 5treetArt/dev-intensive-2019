package ru.skillbranch.devintensive.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.viewmodels.BaseViewModel

public open class BaseActivity: AppCompatActivity() {

    private lateinit var viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_BaseActivity","updateTheme")
        this.delegate.setLocalNightMode(mode)
    }

    fun initViewModel(owner: LifecycleOwner, viewModel: BaseViewModel) {
        this.viewModel = viewModel
        viewModel.getTheme().observe(owner, Observer { updateTheme(it) })
    }
}