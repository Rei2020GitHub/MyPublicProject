package com.sample.hmssample.appauthdemo.model.auth

import com.sample.hmssample.appauthdemo.model.auth.appauth.BaseAppAuth
import com.sample.hmssample.appauthdemo.model.auth.appauth.UserInfo

class GoogleAppAuth private constructor() : BaseAppAuth(
    AUTHORIZATION_ENDPOINT_URI,
    TOKEN_ENDPOINT_URI,
    REVOCATION_ENDPOINT_URI,
    "$REDIRECT_URI_SCHEME:$REDIRECT_URI_PATH",
    CLIENT_ID,
    SCOPE,
    REQUEST_CODE
) {
    companion object {
        private const val AUTHORIZATION_ENDPOINT_URI = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val TOKEN_ENDPOINT_URI = "https://www.googleapis.com/oauth2/v4/token"
        private const val REVOCATION_ENDPOINT_URI = "https://accounts.google.com/o/oauth2/revoke"
        private const val REDIRECT_URI_SCHEME = "com.sample.hmssample.appauthdemo"
        private const val REDIRECT_URI_PATH = "/google"
        private const val CLIENT_ID = "470248566328-skaeakoa1poh1hl6hofha913714nitcu.apps.googleusercontent.com"
        private const val SCOPE = "openid email profile"
        const val REQUEST_CODE = 100

        private var instance: GoogleAppAuth? = null

        fun getInstance(): GoogleAppAuth {
            instance?.let {
                return it
            }
            synchronized(this) {
                return GoogleAppAuth().also { instance = it }
            }
        }
    }

    override fun decodeIdToken(idToken: String, accessToken: String?, refreshToken: String?): UserInfo {
        return UserInfo.parseGoogleIdToken(idToken, accessToken, refreshToken)
    }
}