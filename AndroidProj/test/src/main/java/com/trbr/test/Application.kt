package com.trbr.test

import android.app.Application
import android.util.Log

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("lala ", "onCreate")
    }
}