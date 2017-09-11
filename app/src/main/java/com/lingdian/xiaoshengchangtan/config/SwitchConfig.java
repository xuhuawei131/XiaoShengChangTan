package com.lingdian.xiaoshengchangtan.config;

/**
 * Created by lingdian on 17/9/10.
 */

public class SwitchConfig {

    public static final long connectTime=60000;
    public static final long readtTime=60000;
    public static final long writetTime=60000;
    public static final long pingTime=60000;

    //是否循环播放
    public static boolean isLoop=false;
    //是否跳过头播放
    public static boolean isSkipHead=true;
    //跳过头的时间  毫秒
    public static int SkipHeadTime =300000;//



}
