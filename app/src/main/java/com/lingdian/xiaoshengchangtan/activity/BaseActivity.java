package com.lingdian.xiaoshengchangtan.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.lingdian.xiaoshengchangtan.R;
import com.roger.catloadinglibrary.CatLoadingView;

/**
 * Created by lingdian on 17/9/13.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract void init();
    protected abstract int  setContentView();
    protected abstract void findViewByIds();
    protected abstract void requestService();
    private CatLoadingView mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        int resLayout=setContentView();
        setContentView(resLayout);
        findViewByIds();
        requestService();

    }
    protected void showProgressDialog(String content){
        if(TextUtils.isEmpty(content)){
            content="加载中...";
        }
        if(mView==null){
            mView = new CatLoadingView();
        }

        if(!mView.isAdded()){
            mView.show(getSupportFragmentManager(), content);
        }
    }
    protected void showProgressDialog(){
        showProgressDialog(null);
    }

    protected void disProgressDialog(){
        if (mView!=null&&mView.isAdded()){
            mView.dismiss();
        }
    }
}
