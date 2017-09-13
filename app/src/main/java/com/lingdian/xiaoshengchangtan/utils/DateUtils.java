package com.lingdian.xiaoshengchangtan.utils;

/**
 * Created by lingdian on 17/9/12.
 */

public class DateUtils {
    /*根据时长格式化称时间文本*/
    public static String duration2Time(int duration) {
        int hour= duration /1000/3600;

        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        return (hour < 10 ? "0" + hour : hour + "") + ":"+(min < 10 ? "0" + min : min + "") + ":" + (sec < 10 ? "0" + sec : sec + "");
    }
}
