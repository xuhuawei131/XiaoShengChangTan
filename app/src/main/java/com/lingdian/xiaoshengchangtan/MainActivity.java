package com.lingdian.xiaoshengchangtan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.activity.HomePageActivity;
import com.lingdian.xiaoshengchangtan.activity.NetEasyActivity;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.enums.TimerType;
import com.lingdian.xiaoshengchangtan.player.MyPlayerApi;
import com.lingdian.xiaoshengchangtan.services.TimerService;
import com.lingdian.xiaoshengchangtan.utils.HtmlParer;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String url = "http://gb.jlradio.net/misc/2017-09/06/cms187770article.shtml";
    private static final String homeUrl="http://gb.jlradio.net/misc/node_153_2.shtml";
    private String fileUrl = null;
    private String filePath = null;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View btn_request = findViewById(R.id.btn_request);
        btn_request.setOnClickListener(this);


        View btn_download = findViewById(R.id.btn_download);
        btn_download.setOnClickListener(this);

        View btn_onine_play = findViewById(R.id.btn_onine_play);
        btn_onine_play.setOnClickListener(this);

        View btn_local_play = findViewById(R.id.btn_local_play);
        btn_local_play.setOnClickListener(this);

        View btn_stop = findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(this);

        View btn_page=findViewById(R.id.btn_page);
        btn_page.setOnClickListener(this);


        View btn_junp_home=findViewById(R.id.btn_junp_home);
        btn_junp_home.setOnClickListener(this);

        View btn_junp_neteasy=findViewById(R.id.btn_junp_neteasy);
        btn_junp_neteasy.setOnClickListener(this);

        View btn_test_timer=findViewById(R.id.btn_test_timer);
        btn_test_timer.setOnClickListener(this);

        textResult = (TextView) findViewById(R.id.text_result);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request:
                requestNetPageData();
                break;
            case R.id.btn_download:
                requestDownLoadFile(fileUrl);
                break;
            case R.id.btn_onine_play:
                playMusic(fileUrl);
                break;
            case R.id.btn_local_play:
                playMusic(filePath);
                break;
            case R.id.btn_stop:
                stopMusic();
                break;
            case R.id.btn_page:
                requestNetFileList();
                break;
            case R.id.btn_junp_home:
                startActivity(new Intent(this, HomePageActivity.class));
                break;
            case R.id.btn_junp_neteasy:
                startActivity(new Intent(this, NetEasyActivity.class));
                break;
            case R.id.btn_test_timer:
                TimerService.startTimer(TimerType.TIMER_TEST);
                break;

        }
    }

    private void requestNetPageData() {
        OkGo.<String>post(url).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dealPageResult(response);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                super.onCacheSuccess(response);
                dealPageResult(response);
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

    private void dealPageResult(Response<String> response) {
        Log.v("xhw", "onSuccess");
        String html = response.body();
//        InputStream inputStream=response.getRawResponse().body().byteStream();
        fileUrl = HtmlParer.getPageDownFile(html);
    }

    private List<DownLoadDbBean> dealFileListResult(Response<String> response){


        String html = response.body();
        Document documentAll=Jsoup.parse(html);

        Element elementId=documentAll.getElementById("2016/07/21/111_3_CMSTitleList_153.txt");
        Elements elementIdChild= elementId.children();

        Element elementUl =elementIdChild.get(0);
        Elements elementLiList=elementUl.children();
        int length=elementLiList.size();

        List<DownLoadDbBean> arrrayList=new ArrayList<>();


        for(int i=0;i<length;i++){
            Element elementLi= elementLiList.get(i);

            Elements elementsLi=elementLi.children();

            Element elementA=elementsLi.get(0);
            Element elementSpan=elementsLi.get(1);

            DownLoadDbBean bean=new DownLoadDbBean();

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


    private void requestNetFileList() {
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
    private void requestDownLoadFile(String fileUrl) {
        if (TextUtils.isEmpty(fileUrl)) {
            Toast.makeText(this, "url not empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        OkGo.<File>post(fileUrl).tag(this).execute(new FileCallback() {
            @Override
            public void onSuccess(Response<File> response) {
                filePath = response.body().getPath();
                textResult.append("download file onSuccess " + filePath);
                Log.v("xhw", "onSuccess download: " + filePath);
//                InputStream inputStream=response.getRawResponse().body().byteStream();
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                int code = response.code();
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                float progressPercent = progress.fraction;

                Log.v("xhw", "downloadProgress " + progressPercent);
                textResult.setText("progressPercent:" + progressPercent);
                textResult.append("\n");
            }
        });

    }




    private void playMusic(String fileUri) {
//        MyPlayerApi.getInstance().loadUri(fileUri);

    }

    private void stopMusic() {
        MyPlayerApi.getInstance().stop();
    }


}
