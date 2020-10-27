package com.sample.hmssample.camera2.ui.main

import android.hardware.camera2.CameraCharacteristics
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val cameraType = MutableLiveData<Int>(CameraCharacteristics.LENS_FACING_BACK)
    var surfaceWidth = 1
    var surfaceHeight = 1
}