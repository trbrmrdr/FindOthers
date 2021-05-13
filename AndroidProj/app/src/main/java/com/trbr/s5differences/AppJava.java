package com.trbr.s5differences;
import android.app.Application;
import com.trbr.s5differences.Helper.LogUtils;
import om.trbr.s5differences.EventManager;

public class AppJava extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("on create");
        App.init(this);
        EventManager.INSTANCE.init();

    }
}
