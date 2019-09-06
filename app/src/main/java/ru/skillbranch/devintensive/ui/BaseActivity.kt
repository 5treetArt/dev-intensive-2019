package ru.skillbranch.devintensive.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.viewmodels.BaseViewModel

public open class BaseActivity: AppCompatActivity() {

    protected lateinit var viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    protected open fun initViewModel() {
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_BaseActivity","updateTheme")
        this.delegate.setLocalNightMode(mode)
    }
}