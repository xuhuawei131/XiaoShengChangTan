package com.lingdian.xiaoshengchangtan.bean;

import com.lingdian.xiaoshengchangtan.db.tables.DownloadInfoDbBean;

import java.io.Serializable;

/**
 * Created by lingdian on 17/9/24.
 */

public class DownLoadBeanTask implements Serializable {
    public String url;
    public String title;
    public String filePath;
    public long totalSize;
    public long startSize;
    public long nowSize;
    public DownLoadBeanTask(DownloadInfoDbBean bean){
        if(title==null){
            title=bean.title;
            init();
            totalSize=bean.fileSize;
            startSize=bean.fileDownSize;
        }
    }
    public DownLoadBeanTask(String title){
        this.title=title;
        startSize=0;

        nowSize=0;
        init();
    }
    private void init(){
        FileBean bean= FileBean.newInstance(title);
        url=bean.fileUrl;
        filePath=bean.fileDownPath;
    }
}
