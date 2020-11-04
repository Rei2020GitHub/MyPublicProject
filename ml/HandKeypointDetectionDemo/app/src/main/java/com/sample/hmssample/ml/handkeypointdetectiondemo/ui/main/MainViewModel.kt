package com.sample.hmssample.ml.handkeypointdetectiondemo.ui.main

import android.hardware.Camera
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hms.mlsdk.common.LensEngine

class MainViewModel : ViewModel() {
    companion object {
        const val MAX_LEN_WIDTH = 1920
        const val MAX_LEN_HEIGHT = 1920
    }

    val lensType = MutableLiveData(LensEngine.FRONT_LENS)
    val displayDimensionWidth = MutableLiveData(MAX_LEN_WIDTH)
    val displayDimensionHeight = MutableLiveData(MAX_LEN_HEIGHT)
    val fps = MutableLiveData(30.0f)
    val autoFocusEnabled = MutableLiveData(true)
    val flashMode = MutableLiveData(Camera.Parameters.FLASH_MODE_OFF)
    val focusMode = MutableLiveData(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
    val zoom = MutableLiveData(1.0f)

    var surfaceWidth = 0
    var surfaceHeight = 0
}