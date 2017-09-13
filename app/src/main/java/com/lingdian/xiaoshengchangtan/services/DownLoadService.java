package com.lingdian.xiaoshengchangtan.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 下载的服务器
 */
public class DownLoadService extends Service {

    public DownLoadService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }
}
