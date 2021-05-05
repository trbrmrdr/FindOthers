package com.trbr.s5differences

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.trbr.s5differences.Data.Level

class App : Application() {

    init {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

    companion object {
        @JvmStatic
        fun init(app2: App2) {
            data = Level(app2)
            data.load()
            application = app2
        }

        lateinit var application: App2
        lateinit var data: Level

        var activity: Activity? = null
    }

    override fun attachBaseContext(contect: Context) {
//        super.attachBaseContext(LocalManageUtil.setLocal(contect))
//        MultiDex.install(this)

//        application = this
//        scope_ui.launch {
//        data = Level(contect)
//        data.load()
//        }

    }

  /*  var job: Job? = null
    val scope_ui = CoroutineScope(Dispatchers.Main)
    override fun onCreate() {
        super.onCreate()
//        if (!BuildConfig.DEBUG) - выключен в манифесте
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

//        EventManager.init()


    }*/
}