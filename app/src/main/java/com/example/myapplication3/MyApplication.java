package com.example.myapplication3;

import android.app.Application;

import org.xutils.x;

public class MyApplication extends Application {
    public void onCreate(){
        super.onCreate();
        //Xutils初始化
        x.Ext.init(this);
    }
}

