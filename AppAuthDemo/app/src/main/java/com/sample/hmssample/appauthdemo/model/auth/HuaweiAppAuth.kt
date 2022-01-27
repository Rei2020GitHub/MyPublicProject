package com.sample.hmssample.appauthdemo.model.auth

import com.sample.hmssample.appauthdemo.model.auth.appauth.BaseAppAuth
import com.sample.hmssample.appauthdemo.model.auth.appauth.UserInfo

class HuaweiAppAuth private constructor() : BaseAppAuth(
    AUTHORIZATION_ENDPOINT_URI,
    TOKEN_ENDPOINT_URI,
    REVOCATION_ENDPOINT_URI,
    "$REDIRECT_URI_SCHEME:$REDIRECT_URI_PATH",
    CLIENT_ID,
    SCOPE,
    REQUEST_CODE
) {
    companion object {
        private const val AUTHORIZATION_ENDPOINT_URI = "https://oauth-login.cloud.huawei.com/oauth2/v3/authorize"
        private const val TOKEN_ENDPOINT_URI = "https://oauth-login.cloud.huawei.com/oauth2/v3/token"
        private const val REVOCATION_ENDPOINT_URI = "https://oauth-login.cloud.huawei.com/oauth2/v3/revoke"
        private const val CLIENT_ID = "105439535"
        private const val REDIRECT_URI_SCHEME = "com.huawei.apps.$CLIENT_ID"
        private const val REDIRECT_URI_PATH = "/oauth2redirect"
        private const val SCOPE = "openid email profile"
        const val REQUEST_CODE = 200

        private var instance: HuaweiAppAuth? = null

        fun getInstance(): HuaweiAppAuth {
            instance?.let {
                return it
            }
            synchronized(this) {
                return HuaweiAppAuth().also { instance = it }
            }
        }
    }

    override fun decodeIdToken(idToken: String): UserInfo {
        return UserInfo.parseHuaweiIdToken(idToken)
    }
}