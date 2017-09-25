package com.xhwbaselibrary;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.xhwbaselibrary.caches.MyAppContext;
import com.xhwbaselibrary.caches.MyNetStatusHepler;
import com.xhwbaselibrary.crashes.CrashHandler;


/**
 * Created by lingdian on 17/9/25.
 */

public abstract class MyBaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (isAppMainProcess()) {

            MyAppContext.getInstance().init(this);
            CrashHandler.getInstance();
            MyNetStatusHepler.getInstance().register();

            onlyInitOnce();
        }
    }

    /**
     * 判断是否运行在主进程中
     *
     * @return 是否为主进程
     */
    private boolean isAppMainProcess() {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(this, pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else return this.getPackageName().equalsIgnoreCase(process);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 根据Pid得到进程名
     */
    private String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }
    /**
     * 在多进程App中，只在主进程初始化一次的代码放到这里
     */
    public abstract void onlyInitOnce();
}
