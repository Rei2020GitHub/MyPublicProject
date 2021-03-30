package com.sample.hmssample.accountdemo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val isHuaweiMobileServicesAvailable: Boolean) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(isHuaweiMobileServicesAvailable) as T
    }
}