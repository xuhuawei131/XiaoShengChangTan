package com.lingdian.xiaoshengchangtan.requests;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.cache.FileCache;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import java.io.File;

/**
 * Created by lingdian on 17/9/12.
 * 下载文件
 */

public class DownFileRequest {

    public void startRequest(String fileUrl, String fileName) {
        if (TextUtils.isEmpty(fileUrl)) {
            return;
        }
        String fileDirPath = FileCache.getInstance().getFilePath().getAbsolutePath();

        OkGo.<File>post(fileUrl).tag(this).execute(new FileCallback(fileDirPath, fileName) {
            @Override
            public void onSuccess(Response<File> response) {
                String filePath = response.body().getPath();
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
            }
        });

    }
}
