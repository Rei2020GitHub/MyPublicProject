package com.sample.hmssample.authdemo.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Response
import com.android.volley.VolleyError
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.huawei.agconnect.auth.*
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.api.entity.common.CommonConstant
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.sample.hmssample.authdemo.R
import com.sample.hmssample.authdemo.WebViewActivity
import com.sample.hmssample.authdemo.databinding.MainFragmentBinding
import com.sample.hmssample.authdemo.model.GoogleAuth
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import org.json.JSONObject
import java.security.MessageDigest


class MainFragment : Fragment() {

    companion object {
        val TAG: String = this.javaClass.simpleName
        fun newInstance() = MainFragment()

        private const val REQUEST_CODE_SIGN_IN_HUAWEI_ID = 100
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding

    private var user = AGConnectAuth.getInstance().currentUser

    private var huaweiIdAuthService: HuaweiIdAuthService? = null
    private val facebookCallbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<MainFragmentBinding>(
                inflater,
                R.layout.main_fragment,
                container,
                false
        )

        viewModel.avatarUri.observe(viewLifecycleOwner, {
            Picasso
                    .get()
                    .load(it)
                    .fit()
                    .centerInside()
                    .into(binding.imageViewAvatar)
        })
        viewModel.displayName.observe(viewLifecycleOwner, {
            if (null != it) {
                binding.textViewDisplayName.text = it
                binding.profileContainer.visibility = View.VISIBLE
            }
        })
        viewModel.provider.observe(viewLifecycleOwner, {
            if (null == it) {
                binding.profileContainer.visibility = View.GONE
            } else {
                binding.textViewProvider.text = it
                binding.profileContainer.visibility = View.VISIBLE
            }
        })
        viewModel.textLog.observe(viewLifecycleOwner, {
            binding.textLog.text = it
        })

        // Facebookによるログインを実装する場合
        binding.buttonSignInFacebook.setReadPermissions("email", "public_profile")
        binding.buttonSignInFacebook.fragment = this
        binding.buttonSignInFacebook.registerCallback(
                facebookCallbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(result: LoginResult?) {
                        result?.let { result ->
                            val credential = FacebookAuthProvider.credentialWithToken(result.accessToken.token)
                            signIn(credential)
                        }
                    }

                    override fun onCancel() {
                    }

                    override fun onError(error: FacebookException?) {
                        error?.let {
                            viewModel.addLog("Facebook signIn failed: " + it.message)
                        }
                    }
                })

        // Twitterによるログインを実装する場合
        binding.buttonSignInTwitter.callback = object : com.twitter.sdk.android.core.Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                result?.let { result ->
                    val credential = TwitterAuthProvider.credentialWithToken(
                            result.data.authToken.token,
                            result.data.authToken.secret
                    )
                    signIn(credential)
                }
            }

            override fun failure(exception: TwitterException?) {
                exception?.let {
                    viewModel.addLog("Twitter signIn failed: " + it.message)
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        with(binding) {
            buttonSignInHuaweiId.setOnClickListener{ signInHuaweiId() }
            buttonSignInGoogle.setOnClickListener{ signInGoogle() }
        }

        // HUAWEI IDによるログインを実装する場合
        val huaweiIdAuthParamsHelper = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).apply {
            setScopeList(arrayListOf(Scope(CommonConstant.SCOPE.ACCOUNT_BASEPROFILE)))
        }
        val huaweiIdAuthParams: HuaweiIdAuthParams = huaweiIdAuthParamsHelper.setAccessToken().createParams()
        huaweiIdAuthService = HuaweiIdAuthManager.getService(activity, huaweiIdAuthParams)

        // Googleによるログインを実装する場合
        arguments?.getString(GoogleAuth.REDIRECT_URI_KEY_CODE)?.let { code ->
            signInGoogleCode(code)
        }

        super.onActivityCreated(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Facebookによるログインを実装する場合
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)

        // Twitterによるログインを実装する場合
        binding.buttonSignInTwitter.onActivityResult(requestCode, resultCode, data)

        // HUAWEI IDによるログインを実装する場合
        when (requestCode) {
            REQUEST_CODE_SIGN_IN_HUAWEI_ID -> {
                val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
                if (authHuaweiIdTask.isSuccessful) {
                    val credential = HwIdAuthProvider.credentialWithToken(authHuaweiIdTask.result.accessToken)
                    signIn(credential)
                } else {
                    viewModel.addLog("HUAWEI ID signIn failed: " + (authHuaweiIdTask.exception as ApiException).statusCode)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        AGConnectAuth.getInstance().deleteUser()
    }

    private fun signInHuaweiId() {
        huaweiIdAuthService?.let {
            startActivityForResult(it.signInIntent, REQUEST_CODE_SIGN_IN_HUAWEI_ID)
        }
    }

    private fun signInGoogle() {
        val googleAuth = GoogleAuth()
        val link = googleAuth.createAuthLink()

        // 外部ブラウザを使用
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        val intent = Intent(context, WebViewActivity::class.java).apply {
            putExtra("link", link)
        }

        startActivity(intent)
    }

    private fun signInGoogleCode(code: String) {
        val context: Context = context ?: return
        val googleAuth = GoogleAuth()
        googleAuth.getToken(
            context,
            code,
            object : Response.Listener<String>{
                override fun onResponse(response: String?) {
                    response?.let { response ->
                        val jsonObject = JSONObject(response)
                        if (jsonObject.has("id_token")) {
                            val idToken = jsonObject.getString("id_token")
                            val credential = GoogleAuthProvider.credentialWithToken(idToken)
                            signIn(credential)
                        }
                    }
                }
            },
            object : Response.ErrorListener{
                override fun onErrorResponse(error: VolleyError?) {
                    viewModel.addLog("Google signIn failed: $error")
                }
            }
        )
    }

    private fun signOut() {
        AGConnectAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
    }

    private fun signIn(credential: AGConnectAuthCredential) {
        signOut()
        AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener { result ->
            // onSuccess
            user = result.user

            user?.let { user ->
                viewModel.provider.value = user.providerId
                viewModel.avatarUri.value = user.photoUrl
                viewModel.displayName.value = user.displayName
            }
        }.addOnFailureListener {
            viewModel.addLog("Sign in failed: " + it.message)
        }
    }

    private fun getKeyHash(): String? {
        val context: Context = context ?: return null

        val info: PackageInfo? = context.packageManager?.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
        )
        info?.let { info ->
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                return Base64.encodeToString(md.digest(), Base64.DEFAULT)
            }
        }

        return null
    }
}