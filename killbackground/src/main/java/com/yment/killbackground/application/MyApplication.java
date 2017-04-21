package com.yment.killbackground.application;

import android.app.Application;

import com.yment.killbackground.retrofitservice.RetrofitService;

/**
 * Created by Administrator on 2017-4-21.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitService.getInstance();
    }
}
