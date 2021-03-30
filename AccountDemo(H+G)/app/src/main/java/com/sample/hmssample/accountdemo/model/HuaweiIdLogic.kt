package com.sample.hmssample.accountdemo.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

interface HuaweiIdLogic {
    fun signIn(activity: Activity)
    fun signIn(fragment: Fragment)

    fun signOut()

    fun cancelAuthorization(context: Context)

    fun update(context: Context, intent: Intent)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}