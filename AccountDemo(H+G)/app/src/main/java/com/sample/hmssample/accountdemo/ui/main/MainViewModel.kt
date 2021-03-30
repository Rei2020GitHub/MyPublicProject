package com.sample.hmssample.accountdemo.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.hmssample.accountdemo.Utils
import com.sample.hmssample.accountdemo.model.NonHmsHuaweiIdLogicModel
import com.sample.hmssample.accountdemo.model.BaseHuaweiIdLogicModel
import com.sample.hmssample.accountdemo.model.HmsHuaweiIdLogicModel

class MainViewModel(private val isHuaweiMobileServicesAvailable: Boolean) : ViewModel() {
    companion object {
        @JvmField val TAG: String = this.javaClass.simpleName
    }

    val unionId = MutableLiveData<String>()
    val avatarUri = MutableLiveData<String>()
    val displayName = MutableLiveData<String>()
    val textLog = MutableLiveData<String>("")

    val huaweiIdLogicModel: BaseHuaweiIdLogicModel = generateHuaweiIdLogicModel()

    private fun generateHuaweiIdLogicModel(): BaseHuaweiIdLogicModel {
        return if (isHuaweiMobileServicesAvailable) {
            HmsHuaweiIdLogicModel(this)
        } else {
            NonHmsHuaweiIdLogicModel(this)
        }
    }

    fun addLog(message: String) {
        Log.i(TAG, message)
        textLog.value += Utils.getLogStringLine(message)
    }
}