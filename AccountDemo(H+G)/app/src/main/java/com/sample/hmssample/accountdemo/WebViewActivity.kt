package com.sample.hmssample.accountdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sample.hmssample.accountdemo.databinding.WebViewActivityBinding
import com.sample.hmssample.accountdemo.model.NonHmsHuaweiIdLogic

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: WebViewActivityBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate<WebViewActivityBinding>(
                layoutInflater,
                R.layout.web_view_activity,
                null,
                false
        )
        setContentView(binding.root)

        with(binding) {
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    request?.url?.let { url ->
                        if (url.toString().contains(NonHmsHuaweiIdLogic.REDIRECT_URI)) {
                            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                                data = url
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            return true
                        }

                        view?.loadUrl(url.toString())
                    }

                    return false
                }
            }

            intent?.getStringExtra("link")?.let { url ->
                webView.loadUrl(url)
            }
        }

        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }

}