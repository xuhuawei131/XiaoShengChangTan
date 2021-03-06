package com.lingdian.xiaoshengchangtan.activity;


import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.TimerAdapter;
import com.lingdian.xiaoshengchangtan.bean.TimerBean;
import com.lingdian.xiaoshengchangtan.decoration.ItemDecoration;
import com.lingdian.xiaoshengchangtan.enums.TimerType;
import com.lingdian.xiaoshengchangtan.services.MyPlayerService;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_VIEWHOLDER_TIMER;

public class SelectTimerActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TimerAdapter adapter;
    private List<TimerBean> arrayList;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void init() {
        arrayList=new ArrayList<>();
        arrayList.add(new TimerBean("测试1分钟", TimerType.TIMER_TEST,false));
        arrayList.add(new TimerBean("30分钟", TimerType.TIMER_30,false));
        arrayList.add(new TimerBean("60分钟", TimerType.TIMER_60,false));
        arrayList.add(new TimerBean("90分钟", TimerType.TIMER_90,false));
        arrayList.add(new TimerBean("120分钟", TimerType.TIMER_120,false));
        arrayList.add(new TimerBean("节目播放完毕", TimerType.TIMER_END,false));
        arrayList.add(new TimerBean("取消定时", TimerType.TIMER_CANCEL,true));
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_select_timer;
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

        adapter = new TimerAdapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void requestService() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onMyDestory() {
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = ACTION_VIEWHOLDER_TIMER)
    private void onSelectTimer(TimerBean bean){
        MyPlayerService.startTimer(bean.timerType);
        finish();
    }

}
