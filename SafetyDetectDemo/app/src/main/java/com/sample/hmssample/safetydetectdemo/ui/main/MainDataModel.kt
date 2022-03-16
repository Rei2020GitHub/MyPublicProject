package com.sample.hmssample.safetydetectdemo.ui.main

import android.content.pm.PackageInfo

class MainDataModel(
    val packageInfo: PackageInfo,
    var isMaliciousApp: Boolean,
    var apkSha256: String?,
    var apkCategory: Int?
)