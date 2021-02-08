package com.sample.hmssample.adsdemo

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.AudioFocusType
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.splash.SplashAdDisplayListener
import com.huawei.hms.ads.splash.SplashView.SplashAdLoadListener
import com.sample.hmssample.adsdemo.databinding.SplashActivityBinding


class SplashActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SplashActivity"
        private const val AD_TIMEOUT = 5000L
        private const val MSG_AD_TIMEOUT = 1001
    }

    private lateinit var binding: SplashActivityBinding

    private var hasPaused = false

    private val timeoutHandler: Handler = Handler(object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            if (hasWindowFocus()) {
                jump()
            }
            return false
        }
    })

    private val splashAdLoadListener: SplashAdLoadListener = object : SplashAdLoadListener() {
        override fun onAdLoaded() {
            Log.i(TAG, "SplashAdLoadListener onAdLoaded.")
        }

        override fun onAdFailedToLoad(errorCode: Int) {
            Log.i(TAG, "SplashAdLoadListener onAdFailedToLoad, errorCode: $errorCode")
            jump()
        }

        override fun onAdDismissed() {
            Log.i(TAG, "SplashAdLoadListener onAdDismissed.")
            jump()
        }
    }

    private val adDisplayListener: SplashAdDisplayListener =
        object : SplashAdDisplayListener() {
            override fun onAdShowed() {
                Log.i(TAG, "SplashAdDisplayListener onAdShowed.")
            }

            override fun onAdClick() {
                Log.i(TAG, "SplashAdDisplayListener onAdClick.")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        HwAds.init(applicationContext)

        loadAd()
    }

    override fun onRestart() {
        super.onRestart()
        hasPaused = false
        jump()
    }

    override fun onResume() {
        super.onResume()
        binding.splashAdView.resumeView()
    }

    override fun onPause() {
        super.onPause()
        binding.splashAdView.pauseView()
    }

    override fun onStop() {
        timeoutHandler.removeMessages(MSG_AD_TIMEOUT)
        hasPaused = true
        super.onStop()
    }

    override fun onDestroy() {
        binding.splashAdView.destroyView()
        super.onDestroy()
    }

    private fun loadAd() {
        val adParam = AdParam.Builder().build()
        binding.splashAdView.setAdDisplayListener(adDisplayListener)
        binding.splashAdView.setLogoResId(R.drawable.ic_launcher_foreground)
        binding.splashAdView.setMediaNameResId(R.string.app_name)
        binding.splashAdView.setAudioFocusType(AudioFocusType.NOT_GAIN_AUDIO_FOCUS_WHEN_MUTE)
        binding.splashAdView.load(
            getString(R.string.ad_id_splash),
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            adParam,
            splashAdLoadListener
        )
        Log.i(TAG, "End to load ad")

        timeoutHandler.removeMessages(MSG_AD_TIMEOUT)
        timeoutHandler.sendEmptyMessageDelayed(MSG_AD_TIMEOUT, AD_TIMEOUT)
    }

    private fun jump() {
        Log.i(TAG, "jump hasPaused: $hasPaused")
        if (!hasPaused) {
            hasPaused = true
            Log.i(TAG, "jump into application")
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            val mainHandler = Handler()
            mainHandler.postDelayed({ finish() }, 1000)
        }
    }
}