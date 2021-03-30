package com.sample.hmssample.accountdemo.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.auth0.android.jwt.JWT
import com.huawei.hms.common.util.AGCUtils
import com.sample.hmssample.accountdemo.WebViewActivity
import com.sample.hmssample.accountdemo.ui.main.MainViewModel
import org.json.JSONObject


class NonHmsHuaweiIdLogicModel(private val viewModel: MainViewModel) : BaseHuaweiIdLogicModel(
    viewModel
) {

    companion object {
        private const val AUTH_LINK = "https://oauth-login.cloud.huawei.com/oauth2/v3/authorize"
        private const val APP_SECRET = "0d36d26fdf7528def1dd5a773e57b5ff14136ed1b1caf277c5b47e1dd5b2be41"
        private const val SCOPE = "openid+profile"
        const val REDIRECT_URI = "http://www.hmsaccountkit.com"

        private const val GET_ID_TOKEN_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/token"
        private const val CHECK_ID_TOKEN_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/tokeninfo"

        private const val ID_TOKEN_ISSUE = "https://accounts.huawei.com"

        private const val REVOKE_ACCESS_TOKEN_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/revoke"
        private const val KEY_REVOKE_ACCESS_TOKEN_URL = "KEY_REVOKE_ACCESS_TOKEN_URL"

        private const val USE_API_TO_DECODE_API_TOKEN = false
    }

    override fun signIn(activity: Activity) {
        activity.startActivity(generateSignInIntent(activity.applicationContext))
    }

    override fun signIn(fragment: Fragment) {
        fragment.startActivity(generateSignInIntent(fragment.requireContext()))
    }

    private fun generateSignInIntent(context: Context): Intent {
        return Intent(context, WebViewActivity::class.java).apply {
            putExtra("link", createAuthLink(context))
        }
    }

    override fun signOut() {
        viewModel.unionId.value = null
        viewModel.avatarUri.value = null
        viewModel.displayName.value = null
        viewModel.addLog("signOut success")
    }

    override fun cancelAuthorization(context: Context) {
        val queue = Volley.newRequestQueue(context)

        val postRequest: StringRequest = object : StringRequest(
            Method.POST,
            REVOKE_ACCESS_TOKEN_URL,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    viewModel.addLog("cancelAuthorization success")
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    error?.printStackTrace()
                    viewModel.addLog("cancelAuthorization fail")
                }
            }
        ) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                getAccessToken(context)?.let {
                    params["token"] = it
                }
                return params
            }
        }

        queue.add(postRequest)
    }

    override fun update(context: Context, intent: Intent) {
        intent.data?.getQueryParameter("code")?.let { code ->
            signInWithCode(context, code)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    private fun signInWithCode(context: Context, code: String) {
        val context: Context = context ?: return
        getIdToken(
            context,
            code,
            object : Response.Listener<String> {
                override fun onResponse(response: String?) {
                    response?.let { response ->
                        val jsonObject = JSONObject(response)
                        if (jsonObject.has("id_token")) {
                            val accessToken = jsonObject.getString("access_token")
                            saveAccessToken(context, accessToken)

                            val idToken = jsonObject.getString("id_token")
                            decodeIdToken(context, idToken, USE_API_TO_DECODE_API_TOKEN)
                        }
                    }
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    error?.printStackTrace()
                }
            }
        )
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

    private fun createAuthLink(context: Context): String {
        val map: Map<String, String> = hashMapOf(
            "client_id" to AGCUtils.getAppId(context),
            "scope" to SCOPE,
            "response_type" to "code",
            "access_type" to "offline",
            "redirect_uri" to REDIRECT_URI
        )

        return AUTH_LINK + "?" + mapToString(map)
    }

    private fun getIdToken(
        context: Context,
        authorizationCode: String,
        listener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) {
        val queue = Volley.newRequestQueue(context)

        val postRequest: StringRequest = object : StringRequest(
            Method.POST,
            GET_ID_TOKEN_URL,
            listener,
            errorListener
        ) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["grant_type"] = "authorization_code"
                params["client_id"] = AGCUtils.getAppId(context) ?: ""
                params["client_secret"] = APP_SECRET
                params["code"] = authorizationCode
                params["redirect_uri"] = REDIRECT_URI
                return params
            }
        }

        queue.add(postRequest)
    }

    private fun decodeIdToken(context: Context, idToken: String, useApi: Boolean) {
        if (useApi) {
            val jwt = JWT(idToken)
            if (!jwt.issuer.equals(ID_TOKEN_ISSUE)) {
                viewModel.addLog("Id token issuer is " + jwt.issuer + " , not $ID_TOKEN_ISSUE")
            } else if (jwt.audience?.contains(AGCUtils.getAppId(context)) != true) {
                viewModel.addLog("Id token audience is " + jwt.audience + " , not " + AGCUtils.getAppId(context))
            } else {
                viewModel.unionId.value = jwt.subject
                viewModel.avatarUri.value = jwt.claims["picture"]?.asString()
                viewModel.displayName.value = jwt.claims["display_name"]?.asString()

                viewModel.addLog(viewModel.displayName.value + " signIn success ")
            }
        } else {
            decodeIdTokenByApi(
                context,
                idToken,
                object : Response.Listener<String>{
                    override fun onResponse(response: String?) {
                        response?.let {response ->
                            val jsonObject = JSONObject(response)
                            viewModel.unionId.value = jsonObject.optString("sub")
                            viewModel.avatarUri.value = jsonObject.optString("picture")
                            viewModel.displayName.value = jsonObject.optString("display_name")

                            viewModel.addLog(viewModel.displayName.value + " signIn success ")
                        }
                    }
                },
                Response.ErrorListener { error -> error?.printStackTrace() }
            )
        }
    }

    private fun decodeIdTokenByApi(
        context: Context,
        idToken: String,
        listener: Response.Listener<String>,
        errorListener: Response.ErrorListener
    ) {
        val queue = Volley.newRequestQueue(context)

        val postRequest: StringRequest = object : StringRequest(
            Method.POST,
            CHECK_ID_TOKEN_URL,
            listener,
            errorListener
        ) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["id_token"] = idToken
                return params
            }
        }

        queue.add(postRequest)
    }

    private fun saveAccessToken(context: Context, accessToken: String) {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_REVOKE_ACCESS_TOKEN_URL, accessToken)
            .apply()
    }

    private fun getAccessToken(context: Context): String? {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            .getString(KEY_REVOKE_ACCESS_TOKEN_URL, null)
    }
}