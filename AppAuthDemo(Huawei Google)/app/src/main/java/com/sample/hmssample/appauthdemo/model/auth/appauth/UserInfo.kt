package com.sample.hmssample.appauthdemo.model.auth.appauth

import com.auth0.android.jwt.JWT

class UserInfo {
    companion object {
        fun parseGoogleIdToken(idToken: String, accessToken: String?, refreshToken: String?): UserInfo {
            val jwt = JWT(idToken)
            return UserInfo().apply {
                this.openId = jwt.subject
                this.name = jwt.claims["name"]?.asString()
                this.familyName = jwt.claims["family_name"]?.asString()
                this.givenName = jwt.claims["given_name"]?.asString()
                this.pictureUrl = jwt.claims["picture"]?.asString()
                this.email = jwt.claims["email"]?.asString()
                this.emailVerified = jwt.claims["email_verified"]?.asBoolean()
                this.accessToken = accessToken
                this.refreshToken = refreshToken
            }
        }

        fun parseHuaweiIdToken(idToken: String, accessToken: String?, refreshToken: String?): UserInfo {
            val jwt = JWT(idToken)
            return UserInfo().apply {
                this.openId = jwt.subject
                this.name = jwt.claims["display_name"]?.asString()
                this.pictureUrl = jwt.claims["picture"]?.asString()
                this.email = jwt.claims["email"]?.asString()
                this.emailVerified = jwt.claims["email_verified"]?.asBoolean()
                this.accessToken = accessToken
                this.refreshToken = refreshToken
            }
        }
    }

    var openId: String? = null
    var name: String? = null
    var familyName: String? = null
    var givenName: String? = null
    var pictureUrl: String? = null
    var email: String? = null
    var emailVerified: Boolean? = null
    var accessToken: String? = null
    var refreshToken: String? = null
}