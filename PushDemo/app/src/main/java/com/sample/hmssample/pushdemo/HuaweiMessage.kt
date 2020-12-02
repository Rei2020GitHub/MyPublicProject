package com.sample.hmssample.pushdemo

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject


class HuaweiMessage {

    private fun createSendNotificationJsonString(
        pushToken: String,
        title: String,
        body: String,
        importance: String
    ): JSONObject? {
        return JSONObject().apply {
            // true = テスト; false = 本番
            put("validate_only", false)

            val messageJson = JSONObject().apply {
                val notificationJson = JSONObject().apply {
                    put("title", title)
                    put("body", body)
                }
                put("notification", notificationJson)

                val androidJson = JSONObject().apply {
                    val notificationJson = JSONObject().apply {
                        put("title", title)
                        put("body", body)
                        put("importance", importance)

                        val clickActionJson = JSONObject().apply {
                            put("type", 1)
                            put("intent", "#Intent;compo=com.rvr/.Activity;S.W=U;end")
                        }
                        put("click_action", clickActionJson)
                    }
                    put("notification", notificationJson)
                }
                put("android", androidJson)

                val tokenJsonArray = JSONArray().apply {
                    put(pushToken)
                }
                put("token", tokenJsonArray)
            }
            put("message", messageJson)
        }
    }

    fun sendNotification(
        context: Context,
        appId: String,
        pushToken: String,
        accessToken: String,
        title: String,
        body: String,
        importance: String,
        listener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener
    ) {
        val queue = Volley.newRequestQueue(context)
        val param = createSendNotificationJsonString(pushToken, title, body, importance)

        val postRequest: JsonObjectRequest = object : JsonObjectRequest(
            "https://push-api.cloud.huawei.com/v1/$appId/messages:send",
            param,
            listener,
            errorListener) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Authorization"] = "Bearer $accessToken"

                return headers
            }
        }

        queue.add(postRequest)
    }
}