package com.sample.hmssample.authdemo.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.hmssample.authdemo.Utils

class MainViewModel : ViewModel() {
    companion object {
        @JvmField val TAG: String = this.javaClass.simpleName
    }

    val avatarUri = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    val provider = MutableLiveData<String>()
    val textLog = MutableLiveData<String>("")

    fun addLog(message: String) {
        Log.i(TAG, message)
        textLog.value += Utils.getLogStringLine(message)
    }
}