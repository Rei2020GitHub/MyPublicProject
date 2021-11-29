package com.sample.hmssample.remoteconfigdemo.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var boolean = MutableLiveData(true)
    var double = MutableLiveData(1234567890.12)
    var long = MutableLiveData(123456789012L)
    var string = MutableLiveData("String")
}