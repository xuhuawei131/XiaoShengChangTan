package com.lingdian.xiaoshengchangtan;

import android.app.Application;

import com.lingdian.xiaoshengchangtan.crash.CrashHandler;
import com.lingdian.xiaoshengchangtan.player.MyPlayerApi;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
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
        MyPlayerApi.getInstance().init(this);
    }




    private void initHttp() {
        //必须调用初始化
        OkGo.getInstance().init(this);

        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
        params.put("commonParamsKey2", "这里支持中文参数");
        //-----------------------------------------------------------------------------------//

        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
        OkGo.getInstance()
                //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                .setRetryCount(3)
                //这两行同上，不需要就不要加入
                .addCommonHeaders(headers)  //设置全局公共头
                .addCommonParams(params);   //设置全局公共参数


        OkHttpClient client = new OkHttpClient();
        client.newBuilder().readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .pingInterval(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);
        OkGo.getInstance().setOkHttpClient(client);
    }
}
