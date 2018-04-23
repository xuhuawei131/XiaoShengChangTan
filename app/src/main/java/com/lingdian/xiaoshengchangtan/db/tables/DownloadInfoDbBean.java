package com.lingdian.xiaoshengchangtan.db.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;

/**
 * Created by lingdian on 17/9/24.
 */
@DatabaseTable(tableName="downloadinfo")
public class DownloadInfoDbBean {
    /*** 主键*/
    @DatabaseField(generatedId = true)
    public int _id;
    /**数据唯一的索引 标题的md5值**/
    @DatabaseField
    public String itemId;
    /**标题*/
    @DatabaseField
    public String title;
    /**文件大小*/
    @DatabaseField
    public int fileSize;
    /**文件已经下载大小*/
    @DatabaseField
    public int fileDownSize;
    /**下载的进度**/
    @DatabaseField
    public int percent;
    @DatabaseField
    public String fileUrl;
    /**文件名称*/
    @DatabaseField
    public String fileName;
    /**是否下载完成*/
    @DatabaseField
    public int downStatus= SwitchConfig.DOWNLOAD_STATUS_NO;//0 为下载
}
