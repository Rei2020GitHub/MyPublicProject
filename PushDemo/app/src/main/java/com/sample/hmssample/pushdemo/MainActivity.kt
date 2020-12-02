package com.sample.hmssample.pushdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sample.hmssample.pushdemo.ui.main.MainFragment

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

    override fun onStart() {
        super.onStart()

        intent?.let {
            val msgid = intent.getStringExtra("_push_msgid")
            val cmdType = intent.getStringExtra("_push_cmd_type")
            val notifyId = intent.getIntExtra("_push_notifyid", -1)
            val bundle = intent.extras
            if (bundle != null) {
                for (key in bundle.keySet()) {
                    val content = bundle.get(key).toString()
                    Log.i(
                        MainActivity::class.simpleName,
                        "receive data from push, key = $key, content = $content"
                    )
                }
            }
            Log.d(
                MainActivity::class.simpleName,
                "receive data from push, msgId = $msgid, cmd = $cmdType, notifyId = $notifyId"
            )
        }
    }
}