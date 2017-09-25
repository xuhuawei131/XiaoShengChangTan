package com.lingdian.xiaoshengchangtan.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lingdian.xiaoshengchangtan.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.zhongxiaosplash);
//        setContentView(R.layout.activity_splash);
        Observable.just("Anima")
                .subscribeOn(Schedulers.io())
                .delay(2500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
//                        startActivity(new Intent(SplashActivity.this,HomePageActivity.class));
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }
                });
    }
}
