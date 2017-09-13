package com.lingdian.xiaoshengchangtan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AbsListView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.adapters.HomePageAdapter;
import com.lingdian.xiaoshengchangtan.bean.PageBean;
import com.lingdian.xiaoshengchangtan.constants.Constants;
import com.lingdian.xiaoshengchangtan.decoration.ItemDecoration;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity  {

    private RecyclerView recycler;
    private HomePageAdapter adapter;
    private List<PageBean> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recycler=(RecyclerView)findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setBackgroundColor(Color.WHITE);
        ItemDecoration decoration = new ItemDecoration(this, LinearLayoutManager.VERTICAL);
        recycler.addItemDecoration(decoration);

        arrayList=new ArrayList<>();
        adapter=new HomePageAdapter(arrayList);
        recycler.setAdapter(adapter);
        requestNetFileList();

        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = "hometag")
    private void onItemClick(PageBean bean){
        Intent intent=new Intent(this,DetailPageActivity.class);
        intent.putExtra("bean",bean);
        startActivity(intent);
    }

    private void requestNetFileList() {
        OkGo.<String>post(Constants.homeUrl).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dealFileListResult(response);
            }

            @Override
            public void onStart(Request<String, ? extends Request> request) {
                super.onStart(request);

            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                super.onCacheSuccess(response);
                dealFileListResult(response);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                int code = response.code();
                Log.v("xhw", "onError");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.v("xhw", "onFinish");

                adapter.notifyDataSetChanged();


            }
        });
    }

    private List<PageBean> dealFileListResult(Response<String> response){


        String html = response.body();
        Document documentAll= Jsoup.parse(html);

        Element elementId=documentAll.getElementById("2016/07/21/111_3_CMSTitleList_153.txt");
        Elements elementIdChild= elementId.children();

        Element elementUl =elementIdChild.get(0);
        Elements elementLiList=elementUl.children();
        int length=elementLiList.size();



        for(int i=0;i<length;i++){
            Element elementLi= elementLiList.get(i);

            Elements elementsLi=elementLi.children();

            Element elementA=elementsLi.get(0);
            Element elementSpan=elementsLi.get(1);

            PageBean bean=new PageBean();

            String link=elementA.attr("href");
            String title=elementA.text();
            String date=elementSpan.text();

            bean.link=link;
            bean.title=title;

            bean.title=title;
            bean.link=link;
            bean.date=date;
            arrayList.add(bean);

        }
        return arrayList;
    }
}
