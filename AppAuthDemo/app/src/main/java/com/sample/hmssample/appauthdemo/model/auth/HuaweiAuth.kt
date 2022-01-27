package com.sample.hmssample.appauthdemo.model.auth

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.sample.hmssample.appauthdemo.model.auth.appauth.BaseAuth
import com.sample.hmssample.appauthdemo.model.auth.appauth.SignInCallback
import com.sample.hmssample.appauthdemo.model.auth.appauth.SignOutCallback
import com.sample.hmssample.appauthdemo.model.auth.appauth.UserInfo

class HuaweiAuth private constructor() : BaseAuth {
    companion object {
        const val REQUEST_CODE = 300

        private var instance: HuaweiAuth? = null

        fun getInstance(): HuaweiAuth {
            instance?.let {
                return it
            }
            synchronized(this) {
                return HuaweiAuth().also { instance = it }
            }
        }
    }

    private var authManager: HuaweiIdAuthService? = null
    private var authHuaweiId: AuthHuaweiId? = null
    private var signInCallback: SignInCallback? = null

    override fun signIn(fragment: Fragment, signInCallback: SignInCallback?) {
        this.signInCallback = signInCallback
        val param = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setId()
            .setEmail()
            .setProfile()
            .setAuthorizationCode()
            .createParams()
        authManager = HuaweiIdAuthManager.getService(fragment.requireContext(), param)
        authManager?.let {
            fragment.startActivityForResult(it.signInIntent,
                REQUEST_CODE
            )
        }
    }

    override fun signOut(context: Context, signOutCallback: SignOutCallback?) {
        authManager?.let {
            it.cancelAuthorization().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signOutCallback?.signOutSuccess()
                } else {
                    signOutCallback?.signOutFail(null)
                }
            }
        }
    }

    override fun getUserInfo(): UserInfo? {
        authHuaweiId?.let {
            return UserInfo().apply {
                this.openId = it.openId
                this.name = it.displayName
                this.familyName = it.familyName
                this.givenName = it.givenName
                this.pictureUrl = it.avatarUriString
                this.email = it.email
            }
        }

        return null
    }

    override fun onActivityResult(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
                if (authHuaweiIdTask.isSuccessful) {
                    authHuaweiId = authHuaweiIdTask.result
                    val userInfo = getUserInfo()
                    userInfo?.let { userInfo ->
                        signInCallback?.signInSuccess(userInfo)
                    }?: run {
                        signInCallback?.signInFail(null)
                    }
                } else {
                    this.signInCallback?.signInFail(null)
                }
            }
        }
    }

}