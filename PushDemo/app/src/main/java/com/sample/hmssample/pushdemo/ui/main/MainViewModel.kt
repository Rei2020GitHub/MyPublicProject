package com.sample.hmssample.pushdemo.ui.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.VolleyError
import com.huawei.hms.aaid.HmsInstanceId
import com.sample.hmssample.pushdemo.HuaweiCredential
import com.sample.hmssample.pushdemo.HuaweiMessage
import com.sample.hmssample.pushdemo.R
import com.sample.hmssample.pushdemo.Utils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONObject


class MainViewModel : ViewModel() {

    companion object {
        private const val PREFERENCE_NAME = "HMS_PUSH_DEMO"
        private const val KEY_PUSH_TOKEN = "KEY_PUSH_TOKEN"
    }

    val tokenDisplay = MutableLiveData<String>("")
    private var token: String? = null
    var importance: String = "HIGH"

    fun getToken(context: Context, appId: String) {
        tokenDisplay.value = context.getString(R.string.getting_token)
        Single.create<String>{ emitter ->
            val token = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
            if (token.isNotEmpty()) {
                emitter.onSuccess(token)
            } else {
                emitter.onError(Exception("Token is empty"))
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ token ->
                setToken(token)
                context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putString(KEY_PUSH_TOKEN, token)
                    ?.apply()
                Log.d(MainViewModel::class.simpleName, "Token : $token")
            },{
                it.printStackTrace()
                setGetTokenError(context)
            })
    }

    fun removeToken(context: Context, appId: String) {
        tokenDisplay.value = context.getString(R.string.removing_token)
        Completable.create{ emitter ->
            HmsInstanceId.getInstance(context).deleteToken(appId, "HCM")
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                token = null
                context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
                    ?.edit()
                    ?.remove(KEY_PUSH_TOKEN)
                    ?.apply()
                tokenDisplay.value = context.getString(R.string.token_removed)
            }, {
                tokenDisplay.value = context.getString(R.string.remove_token_error)
            })
    }

    fun setToken(token: String) {
        this.token = token
        tokenDisplay.value = token
    }

    fun setGetTokenError(context: Context) {
        tokenDisplay.value = context.getString(R.string.get_token_error)
    }

    fun sendNotification(context: Context, title: String, body: String, importance: String) {
        val pushToken = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(KEY_PUSH_TOKEN, null)
        pushToken?.let {pushToken ->
            val appId = Utils.getAppId(context)
            appId?.let { appId ->
                Single.create<JSONObject?>{ emitter ->
                    val huaweiCredential = HuaweiCredential()

                    huaweiCredential.getToken(
                        context,
                        object : Response.Listener<String>{
                            override fun onResponse(response: String?) {
                                response?.let {response ->
                                    val jsonObject = JSONObject(response)
                                    if (jsonObject.has("access_token")) {
                                        val accessToken = jsonObject.getString("access_token")
                                        val huaweiMessage = HuaweiMessage()

                                        huaweiMessage.sendNotification(
                                            context,
                                            appId,
                                            pushToken,
                                            accessToken,
                                            title,
                                            body,
                                            importance,
                                            object : Response.Listener<JSONObject>{
                                                override fun onResponse(response: JSONObject?) {
                                                    emitter.onSuccess(response)
                                                }
                                            },
                                            object: Response.ErrorListener{
                                                override fun onErrorResponse(error: VolleyError?) {
                                                    error?.printStackTrace()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        object: Response.ErrorListener{
                            override fun onErrorResponse(error: VolleyError?) {
                                error?.printStackTrace()
                            }
                        }
                    )
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it?.let {
                            val keys = it.keys()
                            while (keys.hasNext()) {
                                val key = keys.next()
                                Log.d(HuaweiMessage::class.simpleName, "Key = " + key + ", Value = " + it.opt(key))
                            }
                        }
                    }, {
                        it.printStackTrace()
                    })
            }
        }
    }
}