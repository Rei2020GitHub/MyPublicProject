package com.sample.hmssample.safetydetectdemo.ui.main

import android.content.pm.PackageInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hms.adapter.internal.CommonCode
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsListResp

class MainViewModel : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val mainDataModelArray = MutableLiveData<Array<MainDataModel>>()

    fun updateAppsCheckResult(packageInfoList: List<PackageInfo>, response: MaliciousAppsListResp) {
        if (response.rtnCode == CommonCode.OK) {
            val list = mutableListOf<MainDataModel>()
            packageInfoList.forEach { packageInfo ->
                val mainDataModel = MainDataModel(
                    packageInfo,
                    false,
                    null,
                    null
                )
                list.add(mainDataModel)
            }

            response.maliciousAppsList.forEach { maliciousAppsData ->
                val mainDataModel = list.firstOrNull { it.packageInfo.packageName == maliciousAppsData.apkPackageName }
                mainDataModel?.let {
                    it.isMaliciousApp = true
                    it.apkSha256 = maliciousAppsData.apkSha256
                    it.apkCategory = maliciousAppsData.apkCategory
                }
            }

            mainDataModelArray.postValue(list.toTypedArray())
        } else {
            Log.e(TAG, response.errorReason)
        }
    }
}