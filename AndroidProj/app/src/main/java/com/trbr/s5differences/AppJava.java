package com.trbr.s5differences;

import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import com.trbr.s5differences.Helper.LogUtils;
import om.trbr.s5differences.EventManager;

public class AppJava extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("on create");
        App.init(this);
        EventManager.INSTANCE.init();
    }
}
