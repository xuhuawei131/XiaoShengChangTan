package com.lingdian.xiaoshengchangtan.activity;



import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.HomePageAdapter;
import com.lingdian.xiaoshengchangtan.bean.PageBean;
import com.lingdian.xiaoshengchangtan.utils.HtmlPageUrlUtils;
import com.lingdian.xiaoshengchangtan.utils.HtmlParer;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;


import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends BaseRefreshMoreViewActivity  {

    private List<PageBean> arrayList;
    private int currentIndex=0;
    private boolean isDoingRequest=false;
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

        arrayList=new ArrayList<>();
        HomePageAdapter adapter=new HomePageAdapter(arrayList);
        setAdapter(adapter);
    }
    @Override
    protected void requestService() {
        if(isDoingRequest){
            return;
        }
        isDoingRequest=true;

        OkGo.<String>post(HtmlPageUrlUtils.getPageUrlByIndex(0)).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                arrayList.clear();
                arrayList.addAll(HtmlParer.dealFileListResult(response));
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);

            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                super.onCacheSuccess(response);
                arrayList.clear();
                arrayList.addAll(HtmlParer.dealFileListResult(response));
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                int code = response.code();
                notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isDoingRequest=false;
                notifyDataSetChanged();
                setRefreshFinish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = "hometag")
    private void onItemClick(PageBean bean){
//        Intent intent=new Intent(this,DetailPageActivity.class);
//        intent.putExtra("bean",bean);
//        startActivity(intent);
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
        if(isDoingRequest){
            return;
        }
        isDoingRequest=true;

        OkGo.<String>post(HtmlPageUrlUtils.getPageUrlByIndex(currentIndex)).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                List list=HtmlParer.dealFileListResult(response);
                if(list.size()!=0){
                    currentIndex++;
                    arrayList.addAll(HtmlParer.dealFileListResult(response));
                }else{
                    hasMore=false;
                }

            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                super.onCacheSuccess(response);
                List list=HtmlParer.dealFileListResult(response);
                if(list.size()!=0){
                    currentIndex++;
                    arrayList.addAll(HtmlParer.dealFileListResult(response));
                }else{
                    hasMore=false;
                }
            }
            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                int code = response.code();
                hasMore=true;
                showErrorFootView();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isDoingRequest=false;
                notifyDataSetChanged();
                if(hasMore){
                    showNormalFootView();
                }else{
                    showEndingFootView();
                }
            }
        });

    }
}
