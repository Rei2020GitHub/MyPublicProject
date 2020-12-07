package com.sample.hmssample.mapdemo

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        MapsInitializer.setApiKey(Utils.getApiKey(applicationContext))
    }
}