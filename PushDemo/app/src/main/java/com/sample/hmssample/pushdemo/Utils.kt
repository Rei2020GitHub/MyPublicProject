package com.sample.hmssample.pushdemo

import android.content.Context
import com.huawei.agconnect.config.AGConnectServicesConfig
import java.net.URLEncoder
import java.text.DateFormat
import java.text.SimpleDateFormat

object Utils {
    fun getAppId(context: Context?): String? {
        context?.let {
            return AGConnectServicesConfig.fromContext(it).getString("client/app_id")
        }
        return null
    }

    fun getApiKey(context: Context?): String? {
        context?.let {
            return AGConnectServicesConfig.fromContext(it).getString("client/api_key")
        }
        return null
    }

    fun getApiKeyEncoded(context: Context?): String? {
        context?.let {
            return URLEncoder.encode(getApiKey(it) ?: "", "utf-8")
        }
        return null
    }

    fun getDateString(time: Long): String {
        val formatter: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")
        return formatter.format(time)
    }
}