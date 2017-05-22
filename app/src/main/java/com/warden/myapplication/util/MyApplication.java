package com.warden.myapplication.util;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePalApplication;

/**
 * Created by Warden on 2017/5/21.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        SDKInitializer.initialize(this);
        context = getApplicationContext();
        LitePalApplication.initialize(context);
    }
    public  static Context getContext(){
        return context;
    }
}
