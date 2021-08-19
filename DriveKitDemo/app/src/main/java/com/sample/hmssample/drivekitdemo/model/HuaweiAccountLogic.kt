package com.sample.hmssample.drivekitdemo.model

import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import com.huawei.cloud.services.drive.DriveScopes
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper
import com.huawei.hms.support.account.result.AuthAccount
import com.huawei.hms.support.account.service.AccountAuthService
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.sample.hmssample.drivekitdemo.ui.main.MainFragment
import com.sample.hmssample.drivekitdemo.ui.main.MainViewModel

class HuaweiAccountLogic(private val viewModel: MainViewModel) {

    private var accountAuthService: AccountAuthService? = null

    fun signIn(fragment: Fragment) {
        if (fragment is MainFragment) {
            signInInit(fragment.requireContext())
            accountAuthService?.let { it ->
                fragment.startForResult.launch(it.signInIntent)
            }
        }
    }

    private fun signInInit(context: Context) {
        val scopeList: List<Scope> = listOf(Scope(DriveScopes.SCOPE_DRIVE_APPDATA))
        val param = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setAccessToken()
            .setIdToken()
            .setScopeList(scopeList)
            .createParams()
        accountAuthService = AccountAuthManager.getService(context, param)
    }

    fun signInCallback(result: ActivityResult) {
        val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(result.data)
        if (authHuaweiIdTask.isSuccessful) {
            with (authHuaweiIdTask.result) {
                viewModel.unionId.value = this.unionId
                viewModel.accessToken.value = this.accessToken
                viewModel.avatarUri.value = this.avatarUriString
                viewModel.displayName.value = this.displayName

                viewModel.addLog(this.displayName + " signIn success ")
            }
        } else {
            viewModel.addLog("signIn failed: " + (authHuaweiIdTask.exception as ApiException).statusCode)
        }
    }

    fun slientSignIn() {
        accountAuthService?.let {
            val task = it.silentSignIn()
            task.addOnSuccessListener(OnSuccessListener<AuthAccount> { authAccount ->
                viewModel.accessToken.value = authAccount.accessToken
                viewModel.addLog("silentSignIn success")
            })
            task.addOnFailureListener(OnFailureListener { e ->
                viewModel.addLog("silentSignIn fail:" + e.message)
            })
        }
    }

    fun signOut() {
        accountAuthService?.let {
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

    fun cancelAuthorization() {
        accountAuthService?.let {
            it.cancelAuthorization().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.addLog("cancelAuthorization success")
                } else {
                    viewModel.addLog("cancelAuthorization fail")
                }
            }
        }
    }
}