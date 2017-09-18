package com.lingdian.xiaoshengchangtan.cache;

import android.util.Log;

import com.lingdian.xiaoshengchangtan.db.impls.DownLoadImple;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DOING;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_WAITTING;

/**
 * Created by lingdian on 17/9/15.
 * 下载的队列的管理
 */

public class DownloadManager {
    private static final DownloadManager ourInstance = new DownloadManager();
    //等待队列
    private Queue<DownLoadDbBean> waittingQueue;
    //下载队列
    private List<DownLoadDbBean> workingList;

    public static DownloadManager getInstance() {
        return ourInstance;
    }

    private DownloadManager() {
        waittingQueue = new ConcurrentLinkedQueue<>();

        List<DownLoadDbBean> dbList= DownLoadImple.getInstance().getDownloadList();
        for (DownLoadDbBean bean:dbList){
            waittingQueue.add(bean);
        }

        workingList=new CopyOnWriteArrayList<>();
    }



    /**
     * 获取当前下载列表
     * @return
     */
    public List<DownLoadDbBean> getAllDownList(){
        List<DownLoadDbBean> allDownList=new ArrayList<>();
        for (DownLoadDbBean bean:workingList){
            allDownList.add(bean);
        }
        for (DownLoadDbBean bean:waittingQueue){
            allDownList.add(bean);
        }
        return allDownList;
    }

    /**
     * 添加到等待队列
     * @param bean
     * @return
     */
    public boolean addWaittingQueue(DownLoadDbBean bean){
        if(!isExistWaittingQueue(bean)){
            bean.downStatus=DOWNLOAD_STATUS_WAITTING;
            waittingQueue.add(bean);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 添加到工作的列表
     * @param bean
     * @return
     */
    public boolean addWorkingList(DownLoadDbBean bean){
        if(!isExistWorkingQueue(bean)){
            Log.v("xhw","addWorkingList bean"+bean.title);
            bean.downStatus=DOWNLOAD_STATUS_DOING;
            workingList.add(bean);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 是否在等待队列
     * @param bean
     * @return
     */
    public boolean isExistWaittingQueue(DownLoadDbBean bean){
        return waittingQueue.contains(bean);
    }

    public boolean isExistWorkingQueue(DownLoadDbBean bean){
        return workingList.contains(bean);
    }

    public int getWaittingLength(){
        return waittingQueue.size();
    }

    public int getWorkingLenght(){
        int length=workingList.size();
        Log.v("xhw","getWorkingLenght length: "+length);
        return length;
    }


    public boolean isEmptyWaitting(){
        return waittingQueue.isEmpty();
    }

    public DownLoadDbBean pollWaittingQueue(){
        return waittingQueue.poll();
    }


    public boolean remoteWaittingList(DownLoadDbBean bean){
        return waittingQueue.remove(bean);
    }

    public boolean remoteWorkingList(DownLoadDbBean bean){
        return workingList.remove(bean);
    }


    public interface  OnDownloadListChangeListener{
            public void onDownloadListChange();
    }

}
