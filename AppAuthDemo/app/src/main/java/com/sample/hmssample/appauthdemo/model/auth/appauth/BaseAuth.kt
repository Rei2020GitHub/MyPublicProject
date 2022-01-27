package com.sample.hmssample.appauthdemo.model.auth.appauth

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

interface BaseAuth {
    fun signIn(fragment: Fragment, signInCallback: SignInCallback?)
    fun signOut(context: Context, signOutCallback: SignOutCallback?)

    fun getUserInfo(): UserInfo?

    fun onActivityResult(context: Context, requestCode: Int, resultCode: Int, data: Intent?)
}