package com.trbr.s5differences

import android.app.Activity
import android.app.Application
import android.content.Context
import com.trbr.s5differences.Ads.AdsManager
import com.trbr.s5differences.Data.Level
import com.trbr.s5differences.Helper.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import om.trbr.s5differences.EventManager


//Неработает - всё проверял, 2 раза
//невызывается onCreate
//при чтении Preferences падает из за нетого контекста

//Даже унаследование в java непомогает
open class App : Application() {

    init {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        application = this
    }


    companion object {

        var job: Job? = null
        val scope_ui = CoroutineScope(Dispatchers.Main)

        @JvmStatic
        fun init(app: Application) {
            application = app
            scope_ui.launch {
                Analytics.onLike(4, true)
            }
//            AdsManager
            data = Level(application)
            data.load()
        }

        lateinit var application: Application
        lateinit var data: Level

        var activity: Activity? = null
    }


    override fun onCreate() {
        super.onCreate()
        LogUtils.i("onCreate" + this)

        init(this)
        EventManager.init()
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

    /*
      override fun onCreate() {
          super.onCreate()
  //        if (!BuildConfig.DEBUG) - выключен в манифесте
  //            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

  //        EventManager.init()


      }*/
}