package com.lingdian.xiaoshengchangtan.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;

import java.io.Serializable;

/**
 * Created by 许华维 on 17/9/13.
 * 网络获取数据的bean
 */
@DatabaseTable(tableName="pageinfo")
public class PageInfoDbBean implements Serializable{
    /*** 主键*/
    @DatabaseField(generatedId = true)
    public int _id;
    /**数据唯一的索引 标题的md5值**/
    @DatabaseField
    public String itemId;
    /**标题*/
    @DatabaseField
    public String title;
    /**文件的日期*/
    @DatabaseField
    public String date;
    /**详情页面地址**/
    @DatabaseField
    public String link;
    /**文件地址数据**/
    @DatabaseField
    public String fileUrl;
    /**文件当前播放时间*/
    @DatabaseField
    public int currentTime;


    /**下载的状态*/
    public int downStatus= SwitchConfig.DOWNLOAD_STATUS_NO;//0 为下载
    /**文件总共的时间*/
    public int totalTime;
    /**下载的进度**/
    public float percent;
    /*** 是否正在播放*/
    public boolean isPlaying=false;
    /**缓存进度**/
    public int buffet_percent;

    public PageInfoDbBean(){
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageInfoDbBean that = (PageInfoDbBean) o;
        return itemId != null ? itemId.equals(that.itemId) : that.itemId == null;
    }
}
