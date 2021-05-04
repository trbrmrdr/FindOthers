package com.trbr.s5differences;

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
//import androidx.multidex.MultiDex
import om.trbr.s5differences.EventManager

class S5Application : Application() {
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    companion object {
        lateinit var application: Application
//                lateinit var data: DataRefractor

        var activity: Activity? = null
    }


    override fun attachBaseContext(contect: Context) {
//        super.attachBaseContext(LocalManageUtil.setLocal(contect))
//        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
//        if (!BuildConfig.DEBUG) - выключен в манифесте
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        return

        application = this
        EventManager.init()

//        data = DataRefractor(this)
//        data.load()
    }
}
