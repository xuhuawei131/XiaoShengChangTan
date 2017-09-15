package com.lingdian.xiaoshengchangtan.cache;

import com.lingdian.xiaoshengchangtan.bean.DownLoadDbBean;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lingdian on 17/9/15.
 * 下载的缓存
 */

public class DownloadCache {
    private static final DownloadCache ourInstance = new DownloadCache();
    //等待队列
    private Queue<DownLoadDbBean> waittingQueue;
    //下载队列
    private List<DownLoadDbBean> workingList;



    public static DownloadCache getInstance() {
        return ourInstance;
    }

    private DownloadCache() {
        waittingQueue = new ConcurrentLinkedQueue<>();
        workingList=new CopyOnWriteArrayList<>();
    }

    public boolean addWaittingQueue(DownLoadDbBean bean){
        if(!isExistWaittingQueue(bean)){
            waittingQueue.add(bean);
            return true;
        }else{
            return false;
        }
    }
    public boolean addWorkingList(DownLoadDbBean bean){
        if(!isExistWorkingQueue(bean)){
            workingList.add(bean);
            return true;
        }else{
            return false;
        }
    }

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
        return workingList.size();
    }


    public boolean isEmptyWaitting(){
        return waittingQueue.isEmpty();
    }

    public DownLoadDbBean pollWaittingQueue(){
        return waittingQueue.poll();
    }
    public boolean remoteWorkingList(DownLoadDbBean bean){
        return workingList.remove(bean);
    }
}
