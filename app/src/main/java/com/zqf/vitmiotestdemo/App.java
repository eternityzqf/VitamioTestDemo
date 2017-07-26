package com.zqf.vitmiotestdemo;

import android.app.Application;

import io.vov.vitamio.Vitamio;

/**
 * class from
 * Created by zqf
 * Time 2017/7/25 15:43
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化一次Vitamio
        Vitamio.isInitialized(this);
    }
}
