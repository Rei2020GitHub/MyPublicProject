package com.sample.hmssample.authdemo.model

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class GoogleAuth {
    companion object {

        const val REDIRECT_URI_KEY_CODE = "code"

        private const val AUTH_LINK = "https://accounts.google.com/o/oauth2/auth"
        private const val TOKEN_LINK = "https://oauth2.googleapis.com/token"
        private const val CLIENT_ID = "997214395592-6qmbmd57plkqejepi7v2j3j9t9172ifm.apps.googleusercontent.com"
        private const val CLIENT_SECRET = "YQUF8QGNcsrsVBzgskXciVOX"
        private const val SCOPE = "https://www.googleapis.com/auth/userinfo.profile"
        const val REDIRECT_URI = "https://com.sample.hmssample.authdemo.com/auth"

     }

    private fun mapToString(map: Map<String, String>): String {
        var query = ""
        var count = 0
        map.forEach { (key, value) ->
            if (count > 0) {
                query += "&"
            }
            query += "$key=$value"
            count++
        }

        return query
    }

    fun createAuthLink(): String {
        val map: Map<String, String> = hashMapOf(
                "client_id" to CLIENT_ID,
                "scope" to SCOPE,
                "response_type" to "code",
                "redirect_uri" to REDIRECT_URI
        )

        return AUTH_LINK + "?" + mapToString(map)
    }

    fun getToken(context: Context, code: String, listener: Response.Listener<String>, errorListener: Response.ErrorListener) {
        val queue = Volley.newRequestQueue(context)

        val postRequest: StringRequest = object : StringRequest(
                Method.POST,
                TOKEN_LINK,
                listener,
                errorListener) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["code"] = code
                params["client_id"] = CLIENT_ID
                params["client_secret"] = CLIENT_SECRET
                params["redirect_uri"] = REDIRECT_URI
                params["grant_type"] = "authorization_code"
                return params
            }
        }

        queue.add(postRequest)
    }
}