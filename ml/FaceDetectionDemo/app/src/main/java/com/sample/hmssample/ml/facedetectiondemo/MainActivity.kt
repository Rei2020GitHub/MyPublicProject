package com.sample.hmssample.ml.facedetectiondemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sample.hmssample.ml.facedetectiondemo.ui.main.MainFragment

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