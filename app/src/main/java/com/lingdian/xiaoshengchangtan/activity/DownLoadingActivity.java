package com.lingdian.xiaoshengchangtan.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.DownloadingAdapter;
import com.lingdian.xiaoshengchangtan.decoration.ItemDecoration;

/**
 * 正在下载的的列表
 */
public class DownLoadingActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private DownloadingAdapter adapter;

    @Override
    protected void init() {

    }

    @Override
    protected int setContentView() {
        return R.layout.activity_down_loading;
    }

    @Override
    protected void findViewByIds() {
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setBackgroundColor(Color.WHITE);
        ItemDecoration decoration = new ItemDecoration(this, LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        adapter=new DownloadingAdapter(null);
    }

    @Override
    protected void requestService() {

    }

}
