package com.sample.hmssample.ml.translationdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.common.util.AGCUtils
import com.huawei.hms.mlsdk.common.MLApplication
import com.sample.hmssample.ml.translationdemo.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        MLApplication.getInstance().apiKey = AGConnectServicesConfig.fromContext(applicationContext).getString("client/api_key")
    }
}