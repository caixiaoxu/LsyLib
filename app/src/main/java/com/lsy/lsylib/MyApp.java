package com.lsy.lsylib;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
