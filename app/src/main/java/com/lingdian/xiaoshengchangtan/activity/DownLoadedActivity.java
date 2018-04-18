package com.lingdian.xiaoshengchangtan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.DownloadedAdapter;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.config.SingleCacheData;
import com.lingdian.xiaoshengchangtan.db.impls.PageInfoImple;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.decoration.ItemDecoration;
import com.lingdian.xiaoshengchangtan.services.MyPlayerService;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DELETE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DONE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_ITEM_CLICK;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DONE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_NO;

/**
 * 已经下载完成的列表
 */
public class DownLoadedActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private DownloadedAdapter adapter;
    private List<PageInfoDbBean> arrayList;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setEmptyView(R.id.textEmptyView,this);
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
        List<PageInfoDbBean> dbList= PageInfoImple.getInstance().getDownloadedList();
        arrayList.addAll(dbList);
        adapter.notifyDataSetChanged();
        notifyAdapter();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onMyDestory() {
        EventBus.getDefault().unregister(this);
    }


    private void notifyAdapter() {
        int length = arrayList.size();
        if (length == 0) {
            findViewById(R.id.textEmptyView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textEmptyView).setVisibility(View.GONE);
        }
    }

    @Subscriber(tag = TAG_DOWNLOADING_DELETE)
    private void onDeleteDownloaded(PageInfoDbBean bean){
        FileBean fileBean=FileBean.newInstance(bean.title);

        File file=new File(fileBean.fileDownPath);
        if (file.exists()){
            file.delete();
        }
        arrayList.remove(bean);
        bean.downStatus=DOWNLOAD_STATUS_NO;

        PageInfoImple.getInstance().updateDownloadStatus(bean);
        adapter.notifyDataSetChanged();
        notifyAdapter();
    }

    @Subscriber(tag = TAG_DOWNLOADING_DONE)
    private void onDoneDownloaded(PageInfoDbBean bean){
        arrayList.add(bean);
        bean.downStatus=DOWNLOAD_STATUS_DONE;
        adapter.notifyDataSetChanged();
        notifyAdapter();
//        MyPlayerService.addPlayList(arrayList);
    }
    @Subscriber(tag = TAG_DOWNLOADING_ITEM_CLICK)
    private void onDoneItemClick(PageInfoDbBean bean){

        SingleCacheData.getInstance().setCurrentList(arrayList);
        MyPlayerService.startPlay(bean);

        Intent intent=new Intent();
        intent.setClass(this,DetailPageActivity.class);
        this.startActivity(intent);
    }
}
