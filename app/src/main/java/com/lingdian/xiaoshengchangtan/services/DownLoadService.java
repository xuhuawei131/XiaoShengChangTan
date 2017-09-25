package com.lingdian.xiaoshengchangtan.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.MyApp;
import com.lingdian.xiaoshengchangtan.bean.DownLoadBeanTask;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.db.impls.DownloadInfoImple;
import com.lingdian.xiaoshengchangtan.db.impls.PageInfoImple;
import com.lingdian.xiaoshengchangtan.db.tables.DownloadInfoDbBean;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.cache.DownloadManager;
import com.lingdian.xiaoshengchangtan.cache.FileCache;
import com.lingdian.xiaoshengchangtan.callbacks.MyFileCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.xhwbaselibrary.caches.MyAppContext;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DONE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_ERROR;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_START;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_UPDATE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DONE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_ERROR;

/**
 * 下载的服务器
 */
public class DownLoadService extends Service {
    //
    private static final int num = 2;

    public static void addDownloadTask(PageInfoDbBean bean) {
        Context contxt = MyAppContext.getInstance().getContext();
        if (contxt != null) {
            Intent intent = new Intent(contxt, DownLoadService.class);
            intent.putExtra("addOrDel",true);
            intent.putExtra("bean", bean);
            contxt.startService(intent);
        }
    }
    public static void deleteDownloadTask(PageInfoDbBean bean){
        Context contxt = MyAppContext.getInstance().getContext();
        if (contxt != null) {
            Intent intent = new Intent(contxt, DownLoadService.class);
            intent.putExtra("addOrDel",false);
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
            PageInfoDbBean bean = (PageInfoDbBean) intent.getSerializableExtra("bean");
            boolean addOrDel=intent.getBooleanExtra("addOrDel",true);
            if(bean!=null){
                boolean isExistWaitting = DownloadManager.getInstance().isExistWaittingQueue(bean);
                boolean isExistWorkting = DownloadManager.getInstance().isExistWorkingQueue(bean);

                if (!isExistWaitting && !isExistWorkting) {
                    PageInfoImple.getInstance().updateDownloadStatus(bean);
                    DownloadManager.getInstance().addWaittingQueue(bean);
                    startNextDownload();
                }else{//任务已存在
                    if(!addOrDel){
                        if(isExistWaitting){
                            DownloadManager.getInstance().remoteWaittingList(bean);
                        }else{
                            OkGo.getInstance().cancelTag(bean.title);
                            DownloadManager.getInstance().remoteWorkingList(bean);
                            startNextDownload();
                        }
                    }
                }
            }else{
                startNextDownload();
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }


    public synchronized void startNextDownload() {

        while (DownloadManager.getInstance().getWorkingLenght() <= num) {
            if (!DownloadManager.getInstance().isEmptyWaitting()) {
                PageInfoDbBean task = DownloadManager.getInstance().pollWaittingQueue();

                DownloadManager.getInstance().addWorkingList(task);
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
    private void startDownload(final PageInfoDbBean bean) {

        final FileBean fileBean = FileBean.newInstance(bean.title);
        String fileUrl = fileBean.fileUrl;

        if (TextUtils.isEmpty(fileUrl)) {
            Toast.makeText(this, "url not empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        PostRequest request=OkGo.<File>post(fileUrl);
        DownloadInfoDbBean info=DownloadInfoImple.getInstance().getDownloadList(bean.title);
        DownLoadBeanTask task;
        if(info!=null){
            task= new DownLoadBeanTask(info);
        }else{
            task=new DownLoadBeanTask(bean.title);
        }

        request.tag(bean.title).execute(new MyFileCallback(task) {
            @Override
            public void onSuccess(Response<File> response) {
                response.getRawResponse().body().contentLength();
                //下载完成
                String filePath = response.body().getPath();
                File downFile = new File(filePath);
                downFile.renameTo(new File(FileCache.getInstance().getDownloadPath(), fileBean.fileName));

                bean.downStatus=DOWNLOAD_STATUS_DONE;
                bean.percent=1;
                EventBus.getDefault().post(bean,TAG_DOWNLOADING_DONE);
                //下载完成
                DownloadManager.getInstance().remoteWorkingList(bean);
                PageInfoImple.getInstance().updateDownloadStatus(bean);

                startNextDownload();
            }

            @Override
            public void onStart(Request<File, ? extends Request> request) {
                super.onStart(request);
                EventBus.getDefault().post(bean,TAG_DOWNLOADING_START);
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                int code = response.code();

                bean.downStatus=DOWNLOAD_STATUS_ERROR;
                EventBus.getDefault().post(bean,TAG_DOWNLOADING_ERROR);
                //下载完成
                DownloadManager.getInstance().remoteWorkingList(bean);
                startNextDownload();

            }

            @Override
            public void onFinish() {
                super.onFinish();

            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                float progressPercent = progress.fraction;
                bean.percent=progressPercent;

                EventBus.getDefault().post(bean,TAG_DOWNLOADING_UPDATE);
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = "onStartTask")
    private void onStartTask(PageInfoDbBean bean) {

    }

    @Subscriber(tag = "onErrorTask")
    private void onErrorTask(PageInfoDbBean bean) {

    }

    @Subscriber(tag = "onFinishTask")
    private void onFinishTask(PageInfoDbBean bean) {
        DownloadManager.getInstance().remoteWorkingList(bean);
        startNextDownload();
    }

}
