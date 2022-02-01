package com.sample.hmssample.appauthdemo.ui.main

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.hmssample.appauthdemo.Utils
import com.sample.hmssample.appauthdemo.model.auth.GoogleAppAuth
import com.sample.hmssample.appauthdemo.model.auth.HuaweiAppAuth
import com.sample.hmssample.appauthdemo.model.auth.appauth.BaseAuth
import com.sample.hmssample.appauthdemo.model.auth.appauth.SignInCallback
import com.sample.hmssample.appauthdemo.model.auth.appauth.SignOutCallback
import com.sample.hmssample.appauthdemo.model.auth.appauth.UserInfo

class MainViewModel : ViewModel() {
    companion object {
        @JvmField val TAG: String = this.javaClass.simpleName
    }

    val pictureUrl = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val openId = MutableLiveData<String>()
    val textLog = MutableLiveData<String>("")

    private val huaweiAuth: BaseAuth = HuaweiAppAuth.getInstance()
    private val googleAuth: BaseAuth = GoogleAppAuth.getInstance()

    fun signInHuaweiId(fragment: Fragment) {
        huaweiAuth.signIn(
            fragment,
            object : SignInCallback {
                override fun signInSuccess(userInfo: UserInfo) {
                    pictureUrl.postValue(userInfo.pictureUrl)
                    name.postValue(userInfo.name)
                    email.postValue(userInfo.email)
                    openId.postValue(userInfo.openId)

                    addLog("[" + huaweiAuth.javaClass.simpleName + "] Sign in Huawei Id success")
                }

                override fun signInFail(exception: Exception?) {
                    addLog("[" + huaweiAuth.javaClass.simpleName + "] Sign in Huawei Id fail")
                    exception?.message?.let {
                        addLog(it)
                    }
                }
            }
        )
    }

    fun signInGoogleId(fragment: Fragment) {
        googleAuth.signIn(
            fragment,
            object : SignInCallback {
                override fun signInSuccess(userInfo: UserInfo) {
                    pictureUrl.postValue(userInfo.pictureUrl)
                    name.postValue(userInfo.name)
                    email.postValue(userInfo.email)
                    openId.postValue(userInfo.openId)

                    addLog("[" + googleAuth.javaClass.simpleName + "] Sign in Google Id success")
                }

                override fun signInFail(exception: Exception?) {
                    addLog("[" + googleAuth.javaClass.simpleName + "] Sign in Google Id fail")
                    exception?.message?.let {
                        addLog(it)
                    }
                }
            }
        )
    }

    fun signOut(context: Context) {
        googleAuth.signOut(
            context,
            object : SignOutCallback {
                override fun signOutSuccess() {
                    addLog("[" + googleAuth.javaClass.simpleName + "] Sign out Google Id success")
                }

                override fun signOutFail(exception: Exception?) {
                    addLog("[" + googleAuth.javaClass.simpleName + "] Sign out Google Id fail")
                    exception?.message?.let {
                        addLog(it)
                    }
                }
            }
        )
        huaweiAuth.signOut(
            context,
            object : SignOutCallback {
                override fun signOutSuccess() {
                    addLog("[" + huaweiAuth.javaClass.simpleName + "] Sign out Huawei Id success")
                }

                override fun signOutFail(exception: Exception?) {
                    addLog("[" + huaweiAuth.javaClass.simpleName + "] Sign out Huawei Id fail")
                    exception?.message?.let {
                        addLog(it)
                    }
                }
            }
        )
    }

    fun onActivityResult(context: Context, requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let { data ->
            when (requestCode) {
                GoogleAppAuth.REQUEST_CODE -> {
                    googleAuth.onActivityResult(context, requestCode, resultCode, data)
                }
                HuaweiAppAuth.REQUEST_CODE -> {
                    huaweiAuth.onActivityResult(context, requestCode, resultCode, data)
                }
            }
        }
    }

    fun addLog(message: String) {
        Log.i(TAG, message)
        textLog.value += Utils.getLogStringLine(message)
    }
}