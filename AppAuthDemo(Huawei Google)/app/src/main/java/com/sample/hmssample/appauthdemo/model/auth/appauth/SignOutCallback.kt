package com.sample.hmssample.appauthdemo.model.auth.appauth

interface SignOutCallback {
    fun signOutSuccess()
    fun signOutFail(exception: Exception?)
}