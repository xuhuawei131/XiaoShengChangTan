package com.lingdian.xiaoshengchangtan.config;

import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.db.impls.PageInfoImple;
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
 * 单例的集合
 */

public class SingleCacheData {
    //当前播放的bean
    private PageInfoDbBean currentBean;
    //设置当前的播放列表
    private List<PageInfoDbBean> currentList;
    //定时关闭的类型
    private TimerType currentTimerType;

    private static SingleCacheData instance=null;

    private SingleCacheData(){
        currentTimerType=TimerType.TIMER_CANCEL;
        currentList=new ArrayList<>();
    }
    public static SingleCacheData getInstance(){
        if(instance==null){
            instance=new SingleCacheData();
        }
        return instance;
    }

    /**
     * 获取当前定时的类型
     * @return
     */
    public TimerType getCurrentTimerType() {
        return currentTimerType;
    }

    /**
     * 设置定时关闭的类型
     * @param currentTimerType
     */
    public void setCurrentTimerType(TimerType currentTimerType) {
        this.currentTimerType = currentTimerType;
    }

    /**
     * 获取当前播放的数据bean
     * @return
     */
    public PageInfoDbBean getCurrentPlayBean(){
        return currentBean;
    }
    /**
     * 获取当前的播放列表
     * @return
     */
    public List<PageInfoDbBean> getCurrentList() {
        return currentList;
    }

    /**
     * 清空播放列表
     */
    public void clearCurrentList(){
        if(currentList!=null){
            currentList.clear();
        }
    }

    /**
     * 设置新的播放列表
     * @param currentList
     */
    public void setCurrentList(List<PageInfoDbBean> currentList) {
        this.currentList = currentList;
    }


    /**
     * 播放新的数据bean
     * @param bean
     */
    public void playNewMusic(PageInfoDbBean bean){
        if (currentBean != null) {
            //如果播放的 id与当前id 不一样 那么就播放
            if (!currentBean.itemId.equals(bean.itemId)) {
                //保存更新播放进度
                PageInfoImple.getInstance().updatePlayProgress();

                currentBean = bean;
                //播放下一个新音乐
                EventBus.getDefault().post(bean,TAG_PLAY_UI_START_NEW_MUSIC);

                FileBean fileBean = FileBean.newInstance(bean.title);
                String url;
                if (fileBean.isExistFile()) {
                    url = fileBean.filePath;
                } else {
                    url = bean.fileUrl;
                }
                //加载数据url
                MyPlayerApi.getInstance().loadUri(bean, url);
            }
        }else{
            currentBean = bean;
            //保存更新播放进度
            PageInfoImple.getInstance().updatePlayProgress();
            //播放下一个新音乐
            EventBus.getDefault().post(bean,TAG_PLAY_UI_START_NEW_MUSIC);

            FileBean fileBean = FileBean.newInstance(bean.title);
            String url;
            if (fileBean.isExistFile()) {
                url = fileBean.filePath;
            } else {
                url = bean.fileUrl;
            }
            //加载数据url
            MyPlayerApi.getInstance().loadUri(bean, url);
        }
    }
    /**
     * 获取下一个 数据bean
     * @return
     */
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

    /**
     * 获取最后一个数据bean
     * @return
     */
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
