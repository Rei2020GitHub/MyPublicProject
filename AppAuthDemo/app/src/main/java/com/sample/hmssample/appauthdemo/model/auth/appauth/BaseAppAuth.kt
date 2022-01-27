package com.sample.hmssample.appauthdemo.model.auth.appauth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.android.volley.VolleyError
import com.sample.hmssample.appauthdemo.model.HttpRequest
import net.openid.appauth.*
import java.util.HashMap

abstract class BaseAppAuth(
    authorizationEndpointUri: String,
    tokenEndpointUri: String,
    private val revocationEndpointUri: String,
    private val redirectUri: String,
    private val cliendId: String,
    private val scope: String,
    private val requestCode: Int
): BaseAuth {

    private val appAuthState: AuthState = AuthState.jsonDeserialize("{}")
    private val config = AuthorizationServiceConfiguration(
        Uri.parse(authorizationEndpointUri),
        Uri.parse(tokenEndpointUri)
    )

    private var signInCallback: SignInCallback? = null

    override fun signIn(fragment: Fragment, signInCallback: SignInCallback?) {
        this.signInCallback = signInCallback
        fragment.startActivityForResult(
            AuthorizationService(fragment.requireContext()).getAuthorizationRequestIntent(
                AuthorizationRequest
                    .Builder(
                        config,
                        cliendId,
                        ResponseTypeValues.CODE,
                        Uri.parse(redirectUri)
                    )
                    .setScope(scope)
                    .build()
            ),
            requestCode
        )
    }

    override fun signOut(context: Context, signOutCallback: SignOutCallback?) {
        val accessToken = appAuthState.accessToken ?: return

        val params: MutableMap<String, String> = HashMap()
        params["token"] = accessToken

        val httpRequest = HttpRequest()
        httpRequest.postFormUrlencodedRequest(
            context,
            revocationEndpointUri,
            null,
            params,
            object : HttpRequest.StringResponseCallback() {
                override fun onResponse(response: String?) {
                    resetAppAuthState()
                    signOutCallback?.signOutSuccess()
                }

                override fun onError(error: VolleyError?) {
                    signOutCallback?.signOutFail(error)
                }
            }
        )
    }

    protected fun resetAppAuthState() {
        appAuthState.update(null)
    }

    override fun getUserInfo(): UserInfo? {
        appAuthState.idToken?.let {
            return decodeIdToken(it)
        }
        return null
    }

    override fun onActivityResult(context: Context, requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            this.requestCode -> {
                data?.let { it ->
                    handleAuthorizationResponse(context, it)
                }
            }
        }
    }

    private fun handleAuthorizationResponse(context: Context, data: Intent) {
        val response = AuthorizationResponse.fromIntent(data)
        val exception = AuthorizationException.fromIntent(data)
        appAuthState.update(response, exception)

        if (exception != null || response == null) {
            exception?.printStackTrace()
            return
        }

        AuthorizationService(context)
            .performTokenRequest(response.createTokenExchangeRequest()) { responseWork, authorizationExceptionWork ->
                appAuthState.update(responseWork, authorizationExceptionWork)
                responseWork?.let {
                    appAuthState.idToken?.let { idToken ->
                        val userInfo = getUserInfo()
                        userInfo?.let { userInfo ->
                            this.signInCallback?.signInSuccess(userInfo)
                        }?: run {
                            this.signInCallback?.signInFail(authorizationExceptionWork)
                        }
                    }?: run {
                        this.signInCallback?.signInFail(authorizationExceptionWork)
                    }
                }?: run {
                    this.signInCallback?.signInFail(authorizationExceptionWork)
                }
            }
    }

    protected abstract fun decodeIdToken(idToken: String): UserInfo
}