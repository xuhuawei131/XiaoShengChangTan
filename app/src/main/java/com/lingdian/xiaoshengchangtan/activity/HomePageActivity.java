package com.lingdian.xiaoshengchangtan.activity;


import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.HomePageAdapter;
import com.lingdian.xiaoshengchangtan.bean.DownLoadDbBean;
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

public class HomePageActivity extends BaseRefreshMoreViewActivity implements View.OnClickListener {

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

    /**
     * 解析完数据之后 合并数据 处理本地数据库
     * @param dataList
     */
    private void dealDataCombinDb(List<DownLoadDbBean> dataList) {
        List<com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean> dbList = DownLoadImple.getInstance().getAllDownloadData();

        List<com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean> updateList = new ArrayList<com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean>();

        for (DownLoadDbBean item : dataList) {

            item.isEditStatus=isEditStatus;

            boolean isExit = false;
            for (com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean dbItem : dbList) {
                if (item.title.endsWith(dbItem.title)) {
                    isExit = true;
                    break;
                }
            }
            if (!isExit) {
                com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean newDbItem = new com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean(item);
                updateList.add(newDbItem);
            }
        }
        if (updateList.size() > 0) {
            DownLoadImple.getInstance().inserDownloadData(updateList);
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
        Intent intent = new Intent(this, DetailPageActivity.class);
        intent.putExtra("bean", bean);
        startActivity(intent);
    }

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
                DownLoadService.addDownloadTask(bean);
                bean.isSelected = false;
                bean.downStatus = 1;
            }
        }
        notifyDataSetChanged();
    }
}
