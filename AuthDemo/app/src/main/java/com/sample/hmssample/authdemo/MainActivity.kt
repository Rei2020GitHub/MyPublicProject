package com.sample.hmssample.authdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.sample.hmssample.authdemo.model.GoogleAuth
import com.sample.hmssample.authdemo.ui.main.MainFragment
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance(), MainFragment.TAG)
                    .commitNow()
        }

        // Facebookによるログインを実装する場合
        FacebookSdk.sdkInitialize(applicationContext)

        // Twitterによるログインを実装する場合
        val twitterAuthConfig = TwitterAuthConfig(
                getString(R.string.twitter_app_key),
                getString(R.string.twitter_app_key_secret)
        )
        val twitterConfig: TwitterConfig = TwitterConfig.Builder(applicationContext).twitterAuthConfig(
                twitterAuthConfig
        ).build()
        Twitter.initialize(twitterConfig)

        // Googleによるログインを実装する場合
        intent?.data?.getQueryParameter("code")?.let { code ->
            supportFragmentManager.findFragmentByTag(MainFragment.TAG)?.let { fragment ->
                val bundle = Bundle().apply {
                    putString(GoogleAuth.REDIRECT_URI_KEY_CODE, code)
                }
                fragment.arguments = bundle
            }
        }
    }

    // Twitterによるログインを実装する場合
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        supportFragmentManager.findFragmentByTag(MainFragment.TAG)?.let {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }
}