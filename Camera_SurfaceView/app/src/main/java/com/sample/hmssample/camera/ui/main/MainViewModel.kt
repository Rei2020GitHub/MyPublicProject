package com.sample.hmssample.camera.ui.main

import android.hardware.Camera
import android.hardware.camera2.CameraCharacteristics
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val cameraType = MutableLiveData<Int>(Camera.CameraInfo.CAMERA_FACING_BACK)
    var surfaceWidth = 1
    var surfaceHeight = 1
}