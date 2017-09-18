package com.lingdian.xiaoshengchangtan.activity;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.HomePageAdapter;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.callbacks.ParserStringCallBack;
import com.lingdian.xiaoshengchangtan.db.impls.DownLoadImple;
import com.lingdian.xiaoshengchangtan.services.DownLoadService;
import com.lingdian.xiaoshengchangtan.utils.HtmlPageUrlUtils;
import com.lingdian.xiaoshengchangtan.utils.HtmlParer;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;


import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DELETE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DONE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_ERROR;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_START;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DONE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_WAITTING;

public class HomePageActivity extends BaseRefreshMoreViewActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private List<DownLoadDbBean> arrayList;
    private int currentIndex = 0;
    private boolean isDoingRequest = false;
    private TextView text_title;
    private TextView text_title_right;
    private TextView text_title_left;

    private static final String SELECT_ALL_ITEM = "全选";
    private static final String SELECT_NOT_ALL_ITEM = "全不选";

    private static final String LABEL_DOWN = "下载";
    private static final String LABEL_CANCEL = "取消";
    private static final String LABEL_EDIT = "编辑";
    //默认是否进入选中模式
    private boolean isEditStatus = false;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
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
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle= new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Resources resource=getBaseContext().getResources();
        ColorStateList csl=resource.getColorStateList(R.color.navigation_menu_item_color);
        navigationView.setItemTextColor(csl);

        text_title = (TextView) findViewById(R.id.text_title);
        text_title_left = (TextView) findViewById(R.id.text_title_left);

        text_title_right = (TextView) findViewById(R.id.text_title_right);

        text_title_right.setTag(false);

        text_title.setOnClickListener(this);
        text_title_right.setOnClickListener(this);
        text_title_left.setOnClickListener(this);

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
                setRefreshFinish();
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
     * 解析完数据之后 合并数据 处理本地数据库
     * @param dataList
     */
    private void dealDataCombinDb(List<DownLoadDbBean> dataList) {
        List<DownLoadDbBean> dbList = DownLoadImple.getInstance().getAllDownloadData();

        List<DownLoadDbBean> updateList = new ArrayList<>();

        for (DownLoadDbBean item : dataList) {

            item.isEditStatus=isEditStatus;

            boolean isExit = false;
            for (DownLoadDbBean dbItem : dbList) {
                if (item.title.endsWith(dbItem.title)) {
                    item.downStatus=dbItem.downStatus;
                    item.percent=dbItem.percent;
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
    private void onDownloadStart(DownLoadDbBean bean){
        int index=arrayList.indexOf(bean);
        arrayList.get(index).downStatus=bean.downStatus;
        notifyDataSetChanged();

    }
    @Subscriber(tag = TAG_DOWNLOADING_DONE)
    private void onDownloadDone(DownLoadDbBean bean){
        int index=arrayList.indexOf(bean);
        arrayList.get(index).downStatus=bean.downStatus;
        notifyDataSetChanged();
    }
    @Subscriber(tag = TAG_DOWNLOADING_DELETE)
    private void onDownloadDelete(DownLoadDbBean bean){
        int index=arrayList.indexOf(bean);
        arrayList.get(index).downStatus=bean.downStatus;
        notifyDataSetChanged();
    }
    @Subscriber(tag = TAG_DOWNLOADING_ERROR)
    private void onDownloadError(DownLoadDbBean bean){
        int index=arrayList.indexOf(bean);
        arrayList.get(index).downStatus=bean.downStatus;
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
        if (v == text_title_left) {
            setLeftText();
        } else if (v == text_title_right) {
            boolean isCurrentEdit = (Boolean) v.getTag();
            setEditStatus(isCurrentEdit);
        }else if(v==text_title){
            startActivity(new Intent(this,DownLoadingActivity.class));
        }
    }

    @Subscriber(tag = "hometag")
    private void onItemClick(DownLoadDbBean bean) {
        if(isEditStatus){
            itemChange(bean);
        }else{
            Intent intent = new Intent(this, DetailPageActivity.class);
            intent.putExtra("bean", bean);
            startActivity(intent);
        }
    }

    /**
     * checkbox
     * @param bean
     */
    @Subscriber(tag = "itemChange")
    private void itemChange(DownLoadDbBean bean) {
        bean.isSelected = !bean.isSelected;

        boolean isSelect = isSelectSomeItem();
        if (isSelect) {
            text_title_right.setText(LABEL_DOWN);
        } else {
            text_title_right.setText(LABEL_CANCEL);
        }
        //获取设置左边状态栏的状态
        boolean isSelectAll = isAllSelectItem();
        if (isSelectAll) {
            text_title_left.setText(SELECT_NOT_ALL_ITEM);
        } else {
            text_title_left.setText(SELECT_ALL_ITEM);
        }
        notifyDataSetChanged();
    }

    /**
     * 下载完成
     * @param bean
     */
    @Subscriber(tag=TAG_DOWNLOADING_DONE)
    private void onDownloadFinish(DownLoadDbBean bean){
        int index=arrayList.indexOf(bean);
        arrayList.get(index).downStatus=DOWNLOAD_STATUS_DONE;
        notifyDataSetChanged();
    }
    /**
     * 设置左边全选按钮的状态
     */
    private void setLeftText() {
        boolean isSelectAll = isAllSelectItem();
        if (isSelectAll) {
            text_title_left.setText(SELECT_ALL_ITEM);
        } else {
            text_title_left.setText(SELECT_NOT_ALL_ITEM);
        }
        setAllItemSelectStatus(!isSelectAll);
        notifyDataSetChanged();
    }

    private void setEditStatus(boolean isCurrentEdit) {
        for (DownLoadDbBean bean : arrayList) {
            bean.isEditStatus = !isCurrentEdit;
        }
        if (isCurrentEdit) {//目前属于 编辑状态 编辑状态包括两种 取消 或者下载
            isEditStatus=false;
            text_title_left.setVisibility(View.GONE);
            text_title_right.setText(LABEL_EDIT);
            boolean isSelected = isSelectSomeItem();
            if (isSelected) {
                //那么就去下载
                addDownloadTask();
            }
        } else {//当前是不是编辑状态
            isEditStatus=true;
            text_title_right.setText(LABEL_CANCEL);
            text_title_left.setText(SELECT_ALL_ITEM);
            text_title_left.setVisibility(View.VISIBLE);
        }
        text_title_right.setTag(!isCurrentEdit);

        notifyDataSetChanged();
    }


    /**
     * 设置所有的item的选中状态
     *
     * @param isSelect
     */
    private void setAllItemSelectStatus(boolean isSelect) {
        for (DownLoadDbBean bean : arrayList) {
            bean.isSelected = isSelect;
        }
    }

    /**
     * 查看是否有选中的
     *
     * @return
     */
    private boolean isSelectSomeItem() {
        boolean isSelected = false;
        for (DownLoadDbBean bean : arrayList) {
            if (bean.isSelected) {
                isSelected = true;
                break;
            }
        }
        return isSelected;
    }

    /***
     * 是否全部选中
     * @return
     */
    private boolean isAllSelectItem() {
        boolean isAllSelected = true;
        for (DownLoadDbBean bean : arrayList) {
            if (!bean.isSelected) {
                isAllSelected = false;
                break;
            }
        }
        return isAllSelected;
    }

    /**
     * 添加任务到下载列表
     */
    private void addDownloadTask() {
        for (DownLoadDbBean bean : arrayList) {
            if (bean.isSelected) {
                //添加到下载列表
                bean.downStatus=DOWNLOAD_STATUS_WAITTING;
                DownLoadService.addDownloadTask(bean);
                bean.isSelected = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        if(id==R.id.item_one){
            startActivity(new Intent(this,DownLoadingActivity.class));
        }else if(id==R.id.item_two){
            startActivity(new Intent(this,DownLoadedActivity.class));
        }else if(id==R.id.item_three){

        }else if(id==R.id.item_one){

        }
        return true;
    }
}
