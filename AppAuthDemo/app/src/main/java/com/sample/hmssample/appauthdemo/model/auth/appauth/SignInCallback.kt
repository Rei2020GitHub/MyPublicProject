package com.sample.hmssample.appauthdemo.model.auth.appauth

interface SignInCallback {
    fun signInSuccess(userInfo: UserInfo)
    fun signInFail(exception: Exception?)
}