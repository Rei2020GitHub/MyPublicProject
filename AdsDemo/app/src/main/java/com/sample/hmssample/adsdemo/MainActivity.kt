package com.sample.hmssample.adsdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.huawei.hms.ads.HwAds
import com.sample.hmssample.adsdemo.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        HwAds.init(applicationContext)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}