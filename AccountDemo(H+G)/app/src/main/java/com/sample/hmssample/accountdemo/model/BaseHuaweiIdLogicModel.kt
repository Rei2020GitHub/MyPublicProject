package com.sample.hmssample.accountdemo.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sample.hmssample.accountdemo.ui.main.MainViewModel

abstract class BaseHuaweiIdLogicModel(private val viewModel: MainViewModel) {

    abstract fun signIn(activity: Activity)
    abstract fun signIn(fragment: Fragment)

    abstract fun signOut()

    abstract fun cancelAuthorization(context: Context)

    abstract fun update(context: Context, intent: Intent)

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}