package com.lingdian.xiaoshengchangtan.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.DownloadingAdapter;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.cache.DownloadManager;
import com.lingdian.xiaoshengchangtan.db.impls.PageInfoImple;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.decoration.ItemDecoration;
import com.lingdian.xiaoshengchangtan.services.DownLoadService;
import com.lingdian.xiaoshengchangtan.viewholders.DownloadingViewholder;
import com.xhwbaselibrary.configs.BaseAction;
import com.xhwbaselibrary.enums.NetStatusType;
import com.xhwbaselibrary.tools.MyLocalBroadcast;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DELETE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DONE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_UPDATE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_NO;

/**
 * 正在下载的的列表
 */
public class DownLoadingActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private DownloadingAdapter adapter;
    private List<PageInfoDbBean> arrayList;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void init() {
        arrayList = new ArrayList<>();
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_down_loading;
    }

    @Override
    protected void findViewByIds() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setEmptyView(R.id.textEmptyView,this);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setBackgroundColor(Color.WHITE);
        ItemDecoration decoration = new ItemDecoration(this, LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        adapter = new DownloadingAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void requestService() {

        arrayList.addAll(DownloadManager.getInstance().getAllDownList());
        adapter.notifyDataSetChanged();
        notifyAdapter();

        EventBus.getDefault().register(this);

        IntentFilter intentFilter=new IntentFilter(BaseAction.LOCAL_ACTION_NET_STATUS_CHANGE);
        MyLocalBroadcast.getInstance().register(intentFilter,broadcastReceiver);
    }

    private void notifyAdapter() {
        int length = arrayList.size();
        if (length == 0) {
            findViewById(R.id.textEmptyView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textEmptyView).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onMyDestory() {
        EventBus.getDefault().unregister(this);
        MyLocalBroadcast.getInstance().unRegister(broadcastReceiver);
    }

    @Subscriber(tag = TAG_DOWNLOADING_UPDATE)
    private void onDownloadingChange(PageInfoDbBean bean) {
        int index = arrayList.indexOf(bean);
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);

        if (holder != null) {
            PageInfoDbBean item = arrayList.get(index);
            item.downStatus = bean.downStatus;
            item.percent = bean.percent;
            DownloadingViewholder dHolder = (DownloadingViewholder) holder;
            dHolder.setData(item);
        }
    }

    @Subscriber(tag = TAG_DOWNLOADING_DONE)
    private void onDownloaded(PageInfoDbBean bean) {
        arrayList.remove(bean);
        adapter.notifyDataSetChanged();
        notifyAdapter();
    }

    @Subscriber(tag = TAG_DOWNLOADING_DELETE)
    private void onDeleteDownloaded(PageInfoDbBean bean) {
        FileBean fileBean = FileBean.newInstance(bean.title);

        File file = new File(fileBean.fileDownPath);
        if (file.exists()) {
            file.delete();
        }
        arrayList.remove(bean);
        bean.downStatus = DOWNLOAD_STATUS_NO;
//        PageInfoImple.getInstance().updateDownloadStatus(bean);
        adapter.notifyDataSetChanged();
        notifyAdapter();
        DownLoadService.deleteDownloadTask(bean);
    }


    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null){
                NetStatusType type=(NetStatusType)intent.getSerializableExtra("type");
                if(type!=null){
                    if (type==NetStatusType.NETSTATUS_MOBILE){//连接到4G状态

                    }else if(type==NetStatusType.NETSTATUS_WIFI){//连接到wifi状态

                    }else{//无网络

                    }
                }
            }
        }
    };
}
