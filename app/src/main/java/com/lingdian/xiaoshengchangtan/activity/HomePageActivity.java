package com.lingdian.xiaoshengchangtan.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.HomePageAdapter;
import com.lingdian.xiaoshengchangtan.cache.DownloadManager;
import com.lingdian.xiaoshengchangtan.customview.EmptyRecyclerView;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.callbacks.ParserStringCallBack;
import com.lingdian.xiaoshengchangtan.db.impls.DownLoadImple;
import com.lingdian.xiaoshengchangtan.services.DownLoadService;
import com.lingdian.xiaoshengchangtan.services.MyPlayerService;
import com.lingdian.xiaoshengchangtan.utils.HtmlPageUrlUtils;
import com.lingdian.xiaoshengchangtan.utils.HtmlParer;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;


import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_ADD;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DELETE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DONE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_ERROR;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_START;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_HOME_ITEM_CLICK;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DONE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_WAITTING;

public class HomePageActivity extends BaseRefreshMoreViewActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private List<DownLoadDbBean> arrayList;
    private int currentIndex = 0;
    private boolean isDoingRequest = false;


    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private boolean  isFirstRequest=true;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_home_page;
    }

    @Override
    protected void findRefreshMoreViewByIds() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

//        EmptyRecyclerView recyclerView= (EmptyRecyclerView) mListView;
//        mListView.setEmptyView(R.id.textEmptyView,this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Resources resource = getBaseContext().getResources();
        ColorStateList csl = resource.getColorStateList(R.color.navigation_menu_item_color);
        navigationView.setItemTextColor(csl);


        arrayList = new ArrayList<>();
        HomePageAdapter adapter = new HomePageAdapter(arrayList);
        setAdapter(adapter);
    }

    @Override
    protected void requestService() {
        EventBus.getDefault().register(this);
        if (isDoingRequest) {
            return;
        }
        isDoingRequest = true;

        OkGo.<String>post(HtmlPageUrlUtils.getPageUrlByIndex(0)).tag(this).execute(new ParserStringCallBack<List<DownLoadDbBean>>() {
            @Override
            public List<DownLoadDbBean> parserJson(Response<String> response) {
                List<DownLoadDbBean> dataList = HtmlParer.dealFileListResult(response);
                dealDataCombinDb(dataList);
                return dataList;
            }

            @Override
            public void onResultComing(List<DownLoadDbBean> response) {
                arrayList.clear();
                arrayList.addAll(response);

                disProgressDialog();
                isDoingRequest = false;
                notifyDataSetChanged();
                notifyAdapter();
                setRefreshFinish();
                if(isFirstRequest){
                    isFirstRequest=false;
                    showWaittingDialog();
                }
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
                showProgressDialog();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                int code = response.code();
                notifyAdapter();
                disProgressDialog();
            }
        });
    }

    @Override
    protected void onMyDestory() {
        EventBus.getDefault().unregister(this);
        drawer.removeDrawerListener(toggle);
    }

    /**
     * 如果列表中 有等待中的数据 那么显示对话框
     */
    private void showWaittingDialog(){
        List<DownLoadDbBean> dbList =DownloadManager.getInstance().getAllDownList();
        if(dbList.size()>0){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("是否下载？").setMessage("您有未完成任务，是否下载？").setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DownLoadService.addDownloadTask(null);
                }
            }).setPositiveButton("取消", null);
            AlertDialog dialog=builder.create();
            dialog.show();
        }
    }


    private void notifyAdapter() {
        int length = arrayList.size();
        if (length == 0) {
            findViewById(R.id.textEmptyView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textEmptyView).setVisibility(View.GONE);
        }
    }

    /**
     * 解析完数据之后 合并数据 处理本地数据库
     *
     * @param dataList
     */
    private void dealDataCombinDb(List<DownLoadDbBean> dataList) {
        List<DownLoadDbBean> dbList = DownLoadImple.getInstance().getAllDownloadData();

        List<DownLoadDbBean> updateList = new ArrayList<>();

        for (DownLoadDbBean item : dataList) {
            boolean isExit = false;
            for (DownLoadDbBean dbItem : dbList) {
                if (item.title.endsWith(dbItem.title)) {
                    item.downStatus = dbItem.downStatus;
                    item.currentTime=dbItem.currentTime;
                    item.percent = dbItem.percent;
                    isExit = true;
                    break;
                }
            }
            if (!isExit) {
                updateList.add(item);
            }
        }
        if (updateList.size() > 0) {
            DownLoadImple.getInstance().inserPageDownloadData(updateList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected int getJRefreshLayoutId() {
        return R.id.refreshLayout;
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.recyclerView;
    }


    @Override
    protected RefreshReturnType doStartTask() {
        return RefreshReturnType.UI_THREAD;
    }

    @Override
    protected Object doRefreshTask() {
        requestService();
        return null;
    }

    @Override
    protected void doRefreshEndingTask(Object o) {

    }

    @Subscriber(tag = TAG_DOWNLOADING_START)
    private void onDownloadStart(DownLoadDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = bean.downStatus;
        notifyDataSetChanged();

    }

    @Subscriber(tag = TAG_DOWNLOADING_DONE)
    private void onDownloadDone(DownLoadDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = bean.downStatus;
        notifyDataSetChanged();
    }

    @Subscriber(tag = TAG_DOWNLOADING_DELETE)
    private void onDownloadDelete(DownLoadDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = bean.downStatus;
        notifyDataSetChanged();
    }

    @Subscriber(tag = TAG_DOWNLOADING_ERROR)
    private void onDownloadError(DownLoadDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = bean.downStatus;
        notifyDataSetChanged();
    }

    @Override
    protected void onMoreTask() {
        if (isDoingRequest) {
            return;
        }
        isDoingRequest = true;

        OkGo.<String>post(HtmlPageUrlUtils.getPageUrlByIndex(currentIndex)).tag(this).execute(new ParserStringCallBack<List<DownLoadDbBean>>() {
            @Override
            public List<DownLoadDbBean> parserJson(Response response) {
                List<DownLoadDbBean> dataList = HtmlParer.dealFileListResult(response);
                dealDataCombinDb(dataList);
                return dataList;
            }

            @Override
            public void onResultComing(List<DownLoadDbBean> list) {
                if (list.size() != 0) {
                    currentIndex++;
                    arrayList.addAll(list);
                    hasMore = true;
                } else {
                    hasMore = false;
                }

                isDoingRequest = false;
                notifyDataSetChanged();
                if (hasMore) {
                    showNormalFootView();
                } else {
                    showEndingFootView();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                int code = response.code();
                hasMore = true;
                showErrorFootView();
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Subscriber(tag = TAG_HOME_ITEM_CLICK)
    private void onItemClick(DownLoadDbBean bean) {
        Intent intent = new Intent(this, DetailPageActivity.class);
        intent.putExtra("bean", bean);
        startActivity(intent);
    }


    /**
     * 下载完成
     *
     * @param bean
     */
    @Subscriber(tag = TAG_DOWNLOADING_DONE)
    private void onDownloadFinish(DownLoadDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = DOWNLOAD_STATUS_DONE;
        notifyDataSetChanged();
    }

    @Subscriber(tag = TAG_DOWNLOADING_ADD)
    private void onStartDownloadTask(DownLoadDbBean bean) {
        //添加到下载列表
        bean.downStatus = DOWNLOAD_STATUS_WAITTING;
        notifyDataSetChanged();

        DownLoadService.addDownloadTask(bean);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        item.setChecked(false);
        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.item_one) {
            startActivity(new Intent(this, DownLoadingActivity.class));
        } else if (id == R.id.item_two) {
            startActivity(new Intent(this, DownLoadedActivity.class));
        } else if (id == R.id.item_three) {

        } else if (id == R.id.item_one) {

        }else if (id == R.id.item2_one) {
            finish();
            stopService(new Intent(this, MyPlayerService.class));
        }
        return true;
    }
}
