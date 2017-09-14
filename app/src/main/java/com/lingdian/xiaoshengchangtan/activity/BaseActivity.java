package com.lingdian.xiaoshengchangtan.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lingdian.xiaoshengchangtan.R;

/**
 * Created by lingdian on 17/9/13.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract void init();
    protected abstract int  setContentView();
    protected abstract void findViewByIds();
    protected abstract void requestService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        int resLayout=setContentView();
        setContentView(resLayout);
        findViewByIds();
        requestService();
    }
}
