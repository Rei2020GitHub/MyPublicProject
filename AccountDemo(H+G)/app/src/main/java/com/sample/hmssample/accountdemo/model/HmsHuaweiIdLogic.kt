package com.sample.hmssample.accountdemo.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.sample.hmssample.accountdemo.ui.main.MainViewModel

class HmsHuaweiIdLogic(private val viewModel: MainViewModel) : HuaweiIdLogic {

    companion object {
        private const val REQUEST_CODE_SIGN_IN_AUTHORIZATION_CODE = 1000
    }

    private var authManager: HuaweiIdAuthService? = null

    override fun signIn(activity: Activity) {
        signInInit(activity.applicationContext)
        authManager?.let {
            activity.startActivityForResult(it.signInIntent,
                REQUEST_CODE_SIGN_IN_AUTHORIZATION_CODE
            )
        }
    }

    override fun signIn(fragment: Fragment) {
        signInInit(fragment.requireContext())
        authManager?.let {
            fragment.startActivityForResult(it.signInIntent,
                REQUEST_CODE_SIGN_IN_AUTHORIZATION_CODE
            )
        }
    }

    private fun signInInit(context: Context) {
        val param = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setAuthorizationCode()
            .createParams()
        authManager = HuaweiIdAuthManager.getService(context, param)
    }

    override fun signOut() {
        authManager?.let {
            val signOutTask: Task<Void> = it.signOut()
            signOutTask.addOnSuccessListener {
                viewModel.unionId.value = null
                viewModel.avatarUri.value = null
                viewModel.displayName.value = null
                viewModel.addLog("signOut success")
            }.addOnFailureListener {
                viewModel.addLog("signOut fail")
            }
        }
    }

    override fun cancelAuthorization(context: Context) {
        authManager?.let {
            it.cancelAuthorization().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.addLog("cancelAuthorization success")
                } else {
                    viewModel.addLog("cancelAuthorization fail")
                }
            }
        }
    }

    override fun update(context: Context, intent: Intent) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN_AUTHORIZATION_CODE -> {
                val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
                if (authHuaweiIdTask.isSuccessful) {
                    with (authHuaweiIdTask.result) {
                        viewModel.unionId.value = this.unionId
                        viewModel.avatarUri.value = this.avatarUriString
                        viewModel.displayName.value = this.displayName

                        viewModel.addLog(this.displayName + " signIn success ")
                    }
                } else {
                    viewModel.addLog("signIn failed: " + (authHuaweiIdTask.exception as ApiException).statusCode)
                }
            }
        }
    }
}