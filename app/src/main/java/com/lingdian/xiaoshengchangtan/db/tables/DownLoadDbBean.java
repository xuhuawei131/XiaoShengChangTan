package com.lingdian.xiaoshengchangtan.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;

import java.io.Serializable;

/**
 * Created by lingdian on 17/9/13.
 */
@DatabaseTable(tableName="download")
public class DownLoadDbBean implements Serializable{
    /**
     * 主键
     */
    @DatabaseField(generatedId = true)
    public int _id;
    /**标题*/
    @DatabaseField
    public String title;
    /**文件下载地址*/
    @DatabaseField
    public String fileUrl;
    /**文件下载地址*/
    @DatabaseField
    public String filePath;
    /**文件名称*/
    @DatabaseField
    public String fileName;
    /**文件大小*/
    @DatabaseField
    public long fileSize;
    /**文件已经下载大小*/
    @DatabaseField
    public long fileDownSize;
    /**下载的进度**/
    @DatabaseField
    public float percent;
    /**文件的日期*/
    @DatabaseField
    public String date;
    /**文件当前播放时间*/
    @DatabaseField
    public int currentTime;
    /**文件总共的时间*/
    @DatabaseField
    public int totalTime;
    /**下载的状态*/
    @DatabaseField
    public int downStatus= SwitchConfig.DOWNLOAD_STATUS_NO;//0 为下载
    /**详情页面地址**/
    @DatabaseField
    public String link;

    public boolean isPlaying=false;
    public int buffet_percent;

    public DownLoadDbBean(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownLoadDbBean that = (DownLoadDbBean) o;

        return title != null ? title.equals(that.title) : that.title == null;

    }
}
