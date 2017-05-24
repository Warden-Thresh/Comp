package com.warden.myapplication.util;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePalApplication;

/**
 * Created by Warden on 2017/5/21.
 */

public class Data extends Application {
    private static Context context;
    private double currentLat;
    private double currentLong;

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }

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
