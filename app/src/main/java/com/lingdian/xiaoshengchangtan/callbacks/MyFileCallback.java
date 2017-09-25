package com.lingdian.xiaoshengchangtan.callbacks;

import com.lingdian.xiaoshengchangtan.bean.DownLoadBeanTask;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Progress;

import java.io.File;

import okhttp3.Response;

/**
 * Created by lingdian on 17/9/15.
 */

public abstract class MyFileCallback extends AbsCallback<File> {
    private MyFileConverter convert;    //文件转换类

    public MyFileCallback() {
        this(null);
    }

    public MyFileCallback(DownLoadBeanTask bean) {
        convert = new MyFileConverter(bean);
        convert.setCallback(this);
        }
    @Override
    public File convertResponse(Response response) throws Throwable {
        File file = convert.convertResponse(response);
        response.close();
        return file;
    }

    @Override
    public void downloadProgress(Progress progress) {
        super.downloadProgress(progress);
    }
}
