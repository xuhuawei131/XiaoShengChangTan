package com.lingdian.xiaoshengchangtan.callbacks;

import android.text.TextUtils;

import com.lingdian.xiaoshengchangtan.bean.DownLoadBeanTask;
import com.lingdian.xiaoshengchangtan.cache.FileCache;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.impls.DownloadInfoImple;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.convert.Converter;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.utils.HttpUtils;
import com.lzy.okgo.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by lingdian on 17/9/24.
 */

public class MyFileConverter implements Converter<File> {
    private DownLoadBeanTask bean;
    private Callback<File> callback;        //下载回调

    public MyFileConverter() {
        this(null);
    }

    public MyFileConverter(DownLoadBeanTask bean) {
        this.bean=bean;
    }


    public void setCallback(Callback<File> callback) {
        this.callback = callback;
    }

    @Override
    public File convertResponse(Response response) throws Throwable {
        String url = response.request().url().toString();

        File file = new File(bean.filePath);

        InputStream bodyStream = null;
        byte[] buffer = new byte[8192];
        RandomAccessFile randomFile = null;
        try {
            ResponseBody body = response.body();
            if (body == null) return null;

            bodyStream = body.byteStream();
            Progress progress = new Progress();
            progress.totalSize = body.contentLength();
            progress.fileName = bean.title;
            progress.filePath = file.getAbsolutePath();
            progress.status = Progress.LOADING;
            progress.url = url;
            progress.tag = bean.title;
            bean.totalSize=progress.totalSize;
            DownloadInfoImple.getInstance().updateDownloadTotalSize(bean.title,bean.totalSize);

            int len;
            randomFile = new RandomAccessFile(file,"rw");
            randomFile.seek(bean.startSize);
            while ((len = bodyStream.read(buffer)) != -1) {
                randomFile.write(buffer, 0, len);

                if (callback == null) continue;
                Progress.changeProgress(progress, len, new Progress.Action() {
                    @Override
                    public void call(Progress progress) {
                        DownloadInfoImple.getInstance().updateDownloadSize(progress.tag,progress.currentSize);
                        onProgress(progress);
                    }
                });
            }
            DownloadInfoImple.getInstance().updateDownloadStatus(bean.title, SwitchConfig.DOWNLOAD_STATUS_DONE);
            return file;
        } catch(Exception e){
            DownloadInfoImple.getInstance().updateDownloadStatus(bean.title, SwitchConfig.DOWNLOAD_STATUS_ERROR);
            return file;
        }
        finally {
            IOUtils.closeQuietly(bodyStream);
            IOUtils.closeQuietly(randomFile);
        }
    }

    private void onProgress(final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.downloadProgress(progress);   //进度回调的方法
            }
        });
    }
}
