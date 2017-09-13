package com.lingdian.xiaoshengchangtan.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by lingdian on 17/9/13.
 */
@DatabaseTable(tableName="download")
public class DownLoadDbBean {
    /**
     * 主键
     */
    @DatabaseField(generatedId = true)
    public int _id;
    @DatabaseField
    public String fileUrl;
    @DatabaseField
    public String filePath;
    @DatabaseField
    public String fileName;
    @DatabaseField
    public String date;
    @DatabaseField
    public int currentTime;
    @DatabaseField
    public int totalTime;
    @DatabaseField
    public boolean isDownloaded;
}
