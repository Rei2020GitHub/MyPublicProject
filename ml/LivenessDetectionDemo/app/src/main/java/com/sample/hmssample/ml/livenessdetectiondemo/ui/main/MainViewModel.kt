package com.sample.hmssample.ml.livenessdetectiondemo.ui.main

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val photo = MutableLiveData<Bitmap>()
    val resultText = MutableLiveData<String>()
}