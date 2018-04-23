package com.lingdian.xiaoshengchangtan.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.HomePageAdapter;
import com.lingdian.xiaoshengchangtan.cache.DownloadManager;
import com.lingdian.xiaoshengchangtan.config.SingleCacheData;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.callbacks.ParserStringCallBack;
import com.lingdian.xiaoshengchangtan.db.impls.PageInfoImple;
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

/**
 * 首页面
 * 列表展示所有的即将播放的列表
 */
public class HomePageActivity extends BaseRefreshMoreViewActivity implements View.OnClickListener {

    private List<PageInfoDbBean> arrayList;
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
        View headerLayout = navigationView.inflateHeaderView(R.layout.layout_navi_left);

        View text_downloading=headerLayout.findViewById(R.id.text_downloading);
        View text_downloaded=headerLayout.findViewById(R.id.text_downloaded);
        View text_setting=headerLayout.findViewById(R.id.text_setting);
        View text_exit=headerLayout.findViewById(R.id.text_exit);

        text_downloading.setOnClickListener(this);
        text_downloaded.setOnClickListener(this);
        text_setting.setOnClickListener(this);
        text_exit.setOnClickListener(this);


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

        OkGo.<String>post(HtmlPageUrlUtils.getPageUrlByIndex(0)).tag(this).execute(new ParserStringCallBack<List<PageInfoDbBean>>() {
            @Override
            public List<PageInfoDbBean> parserJson(Response<String> response) {
                List<PageInfoDbBean> dataList = HtmlParer.dealFileListResult(response);
//                dealDataCombinDb(dataList);
                return dataList;
            }
            @Override
            public void onResultComing(List<PageInfoDbBean> response) {
                disProgressDialog();
                isDoingRequest = false;

                arrayList.clear();
                arrayList.addAll(response);

                notifyDataSetChanged();
                notifyEmptyAdapter();
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
                notifyEmptyAdapter();
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
        List<PageInfoDbBean> dbList =DownloadManager.getInstance().getAllDownList();
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

    /**
     * 空提醒
     */
    private void notifyEmptyAdapter() {
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
    private void dealDataCombinDb(List<PageInfoDbBean> dataList) {
        List<PageInfoDbBean> dbList = PageInfoImple.getInstance().getAllPageData();
        List<PageInfoDbBean> updateList = new ArrayList<>();
        for (PageInfoDbBean item : dataList) {
            boolean isExit = false;
            for (PageInfoDbBean dbItem : dbList) {
                if (item.title.endsWith(dbItem.title)) {
                    item.downStatus = dbItem.downStatus;
                    item.currentTime=dbItem.currentTime;
                    item.totalTime=dbItem.totalTime;
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
            PageInfoImple.getInstance().inserPageDownloadDataList(updateList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getRefreshLayoutId() {
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
    protected void doRefreshEndingTask(Object o) {}
    @Subscriber(tag = TAG_DOWNLOADING_START)
    private void onDownloadStart(PageInfoDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = bean.downStatus;
        notifyDataSetChanged();
    }

    @Subscriber(tag = TAG_DOWNLOADING_DONE)
    private void onDownloadDone(PageInfoDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = bean.downStatus;
        notifyDataSetChanged();
    }

    @Subscriber(tag = TAG_DOWNLOADING_DELETE)
    private void onDownloadDelete(PageInfoDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = bean.downStatus;
        notifyDataSetChanged();
    }

    @Subscriber(tag = TAG_DOWNLOADING_ERROR)
    private void onDownloadError(PageInfoDbBean bean) {
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
        OkGo.<String>post(HtmlPageUrlUtils.getPageUrlByIndex(currentIndex)).tag(this).execute(new ParserStringCallBack<List<PageInfoDbBean>>() {
            @Override
            public List<PageInfoDbBean> parserJson(Response response) {
                List<PageInfoDbBean> dataList = HtmlParer.dealFileListResult(response);
//                dealDataCombinDb(dataList);
                return dataList;
            }
            @Override
            public void onResultComing(List<PageInfoDbBean> list) {
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
        if(v.getId()==R.id.text_downloaded){
            startActivity(new Intent(this, DownLoadedActivity.class));
        }else if(v.getId()==R.id.text_downloading){
            startActivity(new Intent(this, DownLoadingActivity.class));
        }else if(v.getId()==R.id.text_setting){

        }else if(v.getId()==R.id.text_exit){
            finish();
            stopService(new Intent(this, MyPlayerService.class));
        }
    }
    @Subscriber(tag = TAG_HOME_ITEM_CLICK)
    private void onItemClick(PageInfoDbBean bean) {
        //缓存播放列表
        SingleCacheData.getInstance().setCurrentList(arrayList);
        //播放制定的音频
        MyPlayerService.startPlay(bean);
        //跳转详情页面
        Intent intent = new Intent(this, DetailPageActivity.class);
        startActivity(intent);
    }
    /**
     * 下载完成
     *
     * @param bean
     */
    @Subscriber(tag = TAG_DOWNLOADING_DONE)
    private void onDownloadFinish(PageInfoDbBean bean) {
        int index = arrayList.indexOf(bean);
        arrayList.get(index).downStatus = DOWNLOAD_STATUS_DONE;
        notifyDataSetChanged();
    }

    @Subscriber(tag = TAG_DOWNLOADING_ADD)
    private void onStartDownloadTask(PageInfoDbBean bean) {
        //添加到下载列表
        bean.downStatus = DOWNLOAD_STATUS_WAITTING;
        notifyDataSetChanged();
        DownLoadService.addDownloadTask(bean);
    }


}
