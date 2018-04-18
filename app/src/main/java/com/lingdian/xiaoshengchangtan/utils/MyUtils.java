package com.lingdian.xiaoshengchangtan.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.security.MessageDigest;

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

    /**
     * 获取字符串的MD5值
     * @param str
     * @return
     */
    public static String getStrMD5(String str){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }
}
