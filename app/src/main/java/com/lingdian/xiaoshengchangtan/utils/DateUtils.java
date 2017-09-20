package com.lingdian.xiaoshengchangtan.utils;

/**
 * Created by lingdian on 17/9/12.
 */

public class DateUtils {
    /*根据时长格式化称时间文本*/
    public static String duration2TimeByMicSecond(int totalMSeconds) {
        totalMSeconds=totalMSeconds/1000;
        int sec = totalMSeconds % 60;
        int min = totalMSeconds / 60 % 60;
        int hour = totalMSeconds / 3600;
        return (hour < 10 ? "0" + hour : hour + "") + ":"+(min < 10 ? "0" + min : min + "") + ":" + (sec < 10 ? "0" + sec : sec + "");
    }
    /*根据时长格式化称时间文本*/
    public static String duration2TimeBySecond(int totalSeconds) {
        int sec = totalSeconds % 60;
        int min = totalSeconds / 60 % 60;
        int hour = totalSeconds / 3600;
        return (hour < 10 ? "0" + hour : hour + "") + ":"+(min < 10 ? "0" + min : min + "") + ":" + (sec < 10 ? "0" + sec : sec + "");
    }
}
