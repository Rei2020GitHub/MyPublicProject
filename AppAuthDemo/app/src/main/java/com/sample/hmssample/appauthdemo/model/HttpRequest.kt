package com.sample.hmssample.appauthdemo.model

import android.content.Context
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class HttpRequest {

    abstract class ResponseCallback<T> {
        abstract fun onResponse(response: T?)
        abstract fun onError(error: VolleyError?)
    }

    abstract class StringResponseCallback : ResponseCallback<String>() {
        abstract override fun onResponse(response: String?)
    }

    abstract class JsonObjectResponseCallback : ResponseCallback<JSONObject>() {
        abstract override fun onResponse(response: JSONObject?)
    }

    fun postFormUrlencodedRequest(
        context: Context,
        url: String,
        headers: MutableMap<String, String>?,
        params: MutableMap<String, String>?,
        callback: StringResponseCallback?
    ) {
        val queue = Volley.newRequestQueue(context)

        val request: StringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener<String> { response -> callback?.onResponse(response) },
            Response.ErrorListener { error -> callback?.onError(error) }
        ) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getHeaders(): MutableMap<String, String> {
                return headers ?: super.getHeaders()
            }

            override fun getParams(): MutableMap<String, String> {
                return params ?: super.getParams()
            }
        }

        queue.add(request)
    }

    fun postJsonRequest(
        context: Context,
        url: String,
        headers: MutableMap<String, String>?,
        body: JSONObject?,
        callback: JsonObjectResponseCallback?
    ) {
        val queue = Volley.newRequestQueue(context)

        val request: JsonObjectRequest = object : JsonObjectRequest(
            url,
            body,
            Response.Listener<JSONObject> { response -> callback?.onResponse(response) },
            Response.ErrorListener { error -> callback?.onError(error) }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return headers ?: super.getHeaders()
            }
        }

        queue.add(request)
    }
}