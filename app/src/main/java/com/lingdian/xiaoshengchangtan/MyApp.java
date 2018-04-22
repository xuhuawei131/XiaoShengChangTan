package com.lingdian.xiaoshengchangtan;

import android.app.Application;

import com.lingdian.xiaoshengchangtan.crash.CrashHandler;
import com.lingdian.xiaoshengchangtan.player.MyPlayerApi;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.tencent.bugly.crashreport.CrashReport;
import com.xhwbaselibrary.MyBaseApp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by lingdian on 17/9/8.
 */

public class MyApp extends MyBaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
//        DatabaseHelper.getHelper().init();
    }

    @Override
    public void onlyInitOnce() {
        initHttp();
        CrashReport.initCrashReport(getApplicationContext(), "593542ef72", false);
        MyPlayerApi.getInstance().init(this);
    }




    private void initHttp() {

    }
}
