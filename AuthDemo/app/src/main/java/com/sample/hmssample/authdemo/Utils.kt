package com.sample.hmssample.authdemo

import android.content.Context
import com.huawei.agconnect.config.AGConnectServicesConfig
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun getAppId(context: Context?): String? {
        context?.let {
            return AGConnectServicesConfig.fromContext(it).getString("client/app_id")
        }
        return null
    }

    fun getNowDate(): String {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")
        val date = Date(System.currentTimeMillis())
        return df.format(date)
    }

    fun getLogStringLine(message: String?): String {
        return getNowDate() + if (message.isNullOrBlank()) "\n" else ":$message\n"
    }
}