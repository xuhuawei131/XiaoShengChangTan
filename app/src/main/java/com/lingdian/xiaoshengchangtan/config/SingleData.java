package com.lingdian.xiaoshengchangtan.config;

import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.enums.TimerType;
import com.lingdian.xiaoshengchangtan.player.MyPlayerApi;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_START_NEW_MUSIC;

/**
 * Created by lingdian on 17/9/22.
 * 单例的集合再次
 */

public class SingleData {

    private static SingleData instance=null;
    private PageInfoDbBean currentBean;
    //设置当前的播放列表
    private List<PageInfoDbBean> currentList;
    private TimerType currentTimerType;
    private SingleData(){
        currentTimerType=TimerType.TIMER_CANCEL;
        currentList=new ArrayList<>();
    }
    public static SingleData getInstance(){
        if(instance==null){
            instance=new SingleData();
        }
        return instance;
    }





    public TimerType getCurrentTimerType() {
        return currentTimerType;
    }

    public void setCurrentTimerType(TimerType currentTimerType) {
        this.currentTimerType = currentTimerType;
    }


    public PageInfoDbBean getDownLoadDbBean(){
        return currentBean;
    }

    public List<PageInfoDbBean> getCurrentList() {
        return currentList;
    }

    public void clearCurrentList(){
        if(currentList!=null){
            currentList.clear();
        }
    }

    public void setCurrentList(List<PageInfoDbBean> currentList) {
        this.currentList = currentList;
    }


    public void playNewMusic(PageInfoDbBean bean){
        if (currentBean != null) {
            if (!currentBean.title.equals(bean.title)) {
                currentBean = bean;
                EventBus.getDefault().post(bean,TAG_PLAY_UI_START_NEW_MUSIC);

                FileBean fileBean = FileBean.newInstance(bean.title);
                String url;
                if (new File(fileBean.filePath).exists()) {
                    url = fileBean.filePath;
                } else {
                    url = fileBean.fileUrl;
                }
                //加载数据url
                MyPlayerApi.getInstance().loadUri(bean, url);
            }
        }else{
            currentBean = bean;
            EventBus.getDefault().post(bean,TAG_PLAY_UI_START_NEW_MUSIC);

            FileBean fileBean = FileBean.newInstance(bean.title);
            String url;
            if (new File(fileBean.filePath).exists()) {
                url = fileBean.filePath;
            } else {
                url = fileBean.fileUrl;
            }
            //加载数据url
            MyPlayerApi.getInstance().loadUri(bean, url);
        }
    }

    public PageInfoDbBean getNextMusic(){
        if(currentList!=null&&currentList.size()>0){
            int index=0;
            if(currentBean!=null){
                int tempIndex=currentList.indexOf(currentBean);
                if(tempIndex>-1){
                    tempIndex++;
                    int length=currentList.size();
                    if(tempIndex<length){
                        index=tempIndex;
                    }
                }
            }
            return (currentList.get(index));
        }
        return null;
    }

    public PageInfoDbBean getLastMusic(){
        if(currentList!=null&&currentList.size()>0){
            int index=0;
            if(currentBean!=null){
                int tempIndex=currentList.indexOf(currentBean);
                tempIndex--;
                if(tempIndex>-1){
                    index=tempIndex;
                }
            }
            return (currentList.get(index));
        }
        return null;
    }
}
