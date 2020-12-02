package com.sample.hmssample.pushdemo

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class HuaweiCredential {
    companion object {
        private const val URL = "https://oauth-login.cloud.huawei.com/oauth2/v2/token"
        private const val APP_SECRET = "b855f2e6cda6d835e6d88bc3424c9a52f87ae3cb8ae7a96466bd835d8ce7b514"
    }

    fun getToken(context: Context, listener: Response.Listener<String>, errorListener: Response.ErrorListener) {
        val queue = Volley.newRequestQueue(context)

        val postRequest: StringRequest = object : StringRequest(
            Method.POST,
            URL,
            listener,
            errorListener) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["grant_type"] = "client_credentials"
                params["client_id"] = Utils.getAppId(context) ?: ""
                params["client_secret"] = APP_SECRET
                return params
            }
        }

        queue.add(postRequest)
    }
}