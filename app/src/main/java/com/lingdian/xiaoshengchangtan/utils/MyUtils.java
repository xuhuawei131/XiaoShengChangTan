package com.lingdian.xiaoshengchangtan.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by lingdian on 17/9/20.
 */

public class MyUtils {
    /**
     *
     * 功能描述：
     * 检测服务是否在运行
     * @author WAH-WAY(xuwahwhy@163.com)
     * <p>创建日期 ：2015年5月29日 下午12:51:33</p>
     *
     * @param context
     * @param serviceName
     * @return
     *
     * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if(serviceName==null){
            return false;
        }
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
