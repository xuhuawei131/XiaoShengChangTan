package com.lingdian.xiaoshengchangtan.requests;

import android.util.Log;

import com.lingdian.xiaoshengchangtan.bean.PageBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingdian on 17/9/12.
 * 解析home网页中 的数据 找到我们的数据列表
 */

public class HomePageDataRequest {

    public void startRequest(String homeUrl){

        OkGo.<String>post(homeUrl).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dealFileListResult(response);
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

        List<PageBean> arrrayList=new ArrayList<>();


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
            arrrayList.add(bean);

        }
        return arrrayList;
    }
}
