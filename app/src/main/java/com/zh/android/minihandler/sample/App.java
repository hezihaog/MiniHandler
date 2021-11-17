package com.zh.android.minihandler.sample;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppMonitor.get().initialize(this);
    }
}