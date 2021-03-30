package com.sample.hmssample.accountdemo.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.hmssample.accountdemo.Utils
import com.sample.hmssample.accountdemo.model.NonHmsHuaweiIdLogic
import com.sample.hmssample.accountdemo.model.HmsHuaweiIdLogic
import com.sample.hmssample.accountdemo.model.HuaweiIdLogic

class MainViewModel(private val isHuaweiMobileServicesAvailable: Boolean) : ViewModel() {
    companion object {
        @JvmField val TAG: String = this.javaClass.simpleName
    }

    val unionId = MutableLiveData<String>()
    val avatarUri = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    val textLog = MutableLiveData<String>("")

    val huaweiIdLogic: HuaweiIdLogic = generateHuaweiIdLogic()

    private fun generateHuaweiIdLogic(): HuaweiIdLogic {
        return if (isHuaweiMobileServicesAvailable) {
            HmsHuaweiIdLogic(this)
        } else {
            NonHmsHuaweiIdLogic(this)
        }
    }

    fun addLog(message: String) {
        Log.i(TAG, message)
        textLog.value += Utils.getLogStringLine(message)
    }
}