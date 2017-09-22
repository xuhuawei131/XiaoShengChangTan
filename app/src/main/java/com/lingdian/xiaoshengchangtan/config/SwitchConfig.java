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


    public static final String URL_HOME="http://gb.jlradio.net";
    //下载的音频文件 文件夹路径
    public static final String PATH_DIR="/xsct/.download";

    public static final String DB_NAME="xsct.db";

    //下载的状态
    public static final int DOWNLOAD_STATUS_NO=0;
    public static final int DOWNLOAD_STATUS_DOING=1;
    public static final int DOWNLOAD_STATUS_PAUSE=2;
    public static final int DOWNLOAD_STATUS_WAITTING=3;
    public static final int DOWNLOAD_STATUS_DONE=4;
    public static final int DOWNLOAD_STATUS_ERROR=5;

    /**
     * 通过下载的状态 转化成str
     * @param status
     * @return
     */
    public static String getDownStatusStr(int status){
        if(status==DOWNLOAD_STATUS_NO){
            return "未下载";
        }else if(status==DOWNLOAD_STATUS_DOING){
            return "下载中";
        }else if(status==DOWNLOAD_STATUS_PAUSE){
            return "暂停中";
        }else if(status==DOWNLOAD_STATUS_WAITTING){
            return "等待中";
        }else if(status==DOWNLOAD_STATUS_DONE){
            return "已下载";
        }else{
            return "异常";
        }
    }

}
