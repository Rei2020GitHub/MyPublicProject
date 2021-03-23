package com.sample.hmssample.ml.livenessdetectiondemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.hmssample.ml.livenessdetectiondemo.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}