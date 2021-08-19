package com.sample.hmssample.drivekitdemo

import java.text.SimpleDateFormat
import java.util.*

object Utils {
    private fun getNowDate(): String {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")
        val date = Date(System.currentTimeMillis())
        return df.format(date)
    }

    fun getLogStringLine(message: String?): String {
        return getNowDate() + if (message.isNullOrBlank()) "\n" else ":$message\n"
    }
}