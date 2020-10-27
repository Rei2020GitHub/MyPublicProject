package com.sample.hmssample.ml.skeletondetectiondemo.ui.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var filePath = MutableLiveData<Uri>(null)
}