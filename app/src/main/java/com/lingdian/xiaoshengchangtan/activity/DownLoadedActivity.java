package com.lingdian.xiaoshengchangtan.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.DownloadedAdapter;
import com.lingdian.xiaoshengchangtan.adapters.DownloadingAdapter;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.cache.DownloadManager;
import com.lingdian.xiaoshengchangtan.db.impls.DownLoadImple;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.decoration.ItemDecoration;
import com.lingdian.xiaoshengchangtan.services.DownLoadService;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DELETE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DONE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DONE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_NO;

/**
 * 已经下载完成的列表
 */
public class DownLoadedActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private DownloadedAdapter adapter;
    private List<DownLoadDbBean> arrayList;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void init() {
        arrayList=new ArrayList<>();
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_down_loaded;
    }

    @Override
    protected void findViewByIds() {
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);

        layoutManager=new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setBackgroundColor(Color.WHITE);
        ItemDecoration decoration = new ItemDecoration(this, LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        adapter=new DownloadedAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void requestService() {
        List<DownLoadDbBean> dbList= DownLoadImple.getInstance().getDownloadedList();
        arrayList.addAll(dbList);
        adapter.notifyDataSetChanged();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onMyDestory() {
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = TAG_DOWNLOADING_DELETE)
    private void onDeleteDownloaded(DownLoadDbBean bean){
        FileBean fileBean=FileBean.checkData(bean.title);

        File file=new File(fileBean.fileDownPath);
        if (file.exists()){
            file.delete();
        }
        arrayList.remove(bean);
        bean.downStatus=DOWNLOAD_STATUS_NO;
        DownLoadImple.getInstance().updateDownloadStatus(bean);
        adapter.notifyDataSetChanged();
    }

    @Subscriber(tag = TAG_DOWNLOADING_DONE)
    private void onDoneDownloaded(DownLoadDbBean bean){
        arrayList.add(bean);
        bean.downStatus=DOWNLOAD_STATUS_DONE;
        adapter.notifyDataSetChanged();
    }
}
