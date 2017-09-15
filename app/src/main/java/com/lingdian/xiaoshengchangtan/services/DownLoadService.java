package com.lingdian.xiaoshengchangtan.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.MyApp;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.bean.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.cache.DownloadCache;
import com.lingdian.xiaoshengchangtan.cache.FileCache;
import com.lingdian.xiaoshengchangtan.cache.MyFileCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;

/**
 * 下载的服务器
 */
public class DownLoadService extends Service {
    //
    private static final int num = 3;


    public static void addDownloadTask(DownLoadDbBean bean) {
        Context contxt = MyApp.application;
        if (contxt != null) {
            Intent intent = new Intent(contxt, DownLoadService.class);
            intent.putExtra("bean", bean);
            contxt.startService(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            DownLoadDbBean bean = (DownLoadDbBean) intent.getSerializableExtra("bean");

            boolean isExistWaitting = DownloadCache.getInstance().isExistWaittingQueue(bean);
            boolean isExistWorkting = DownloadCache.getInstance().isExistWorkingQueue(bean);

            if (!isExistWaitting && !isExistWorkting) {
                DownloadCache.getInstance().addWaittingQueue(bean);
                startNextDownload();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startNextDownload() {

        while (DownloadCache.getInstance().getWorkingLenght() <= num) {

            if (!DownloadCache.getInstance().isEmptyWaitting()) {
                DownLoadDbBean task = DownloadCache.getInstance().pollWaittingQueue();

                DownloadCache.getInstance().addWorkingList(task);
                startDownload(task);
            } else {
                break;
            }
        }
    }

    /**
     * 开始下载任务
     *
     * @param bean
     */
    private void startDownload(final DownLoadDbBean bean) {

        final FileBean fileBean = FileBean.checkData(bean.title);
        String fileUrl = fileBean.fileUrl;

        if (TextUtils.isEmpty(fileUrl)) {
            Toast.makeText(this, "url not empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        OkGo.<File>post(fileUrl).tag(this).execute(new MyFileCallback(fileBean.fileDownName) {
            @Override
            public void onSuccess(Response<File> response) {
                String filePath = response.body().getPath();
                File downFile = new File(filePath);
                downFile.renameTo(new File(FileCache.getInstance().getDownloadPath(), fileBean.fileName));


                //下载完成
                DownloadCache.getInstance().remoteWorkingList(bean);
                startNextDownload();

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
//                    textResult.setText("progressPercent:" + progressPercent);
//                    textResult.append("\n");
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = "onStartTask")
    private void onStartTask(DownLoadDbBean bean) {

    }

    @Subscriber(tag = "onErrorTask")
    private void onErrorTask(DownLoadDbBean bean) {

    }

    @Subscriber(tag = "onFinishTask")
    private void onFinishTask(DownLoadDbBean bean) {
        DownloadCache.getInstance().remoteWorkingList(bean);
        startNextDownload();
    }

}
