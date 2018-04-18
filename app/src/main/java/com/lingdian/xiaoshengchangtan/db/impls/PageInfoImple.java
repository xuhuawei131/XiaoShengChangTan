package com.lingdian.xiaoshengchangtan.db.impls;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.lingdian.xiaoshengchangtan.db.tables.DownloadInfoDbBean;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;

import java.sql.SQLException;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DOING;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DONE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_PAUSE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_WAITTING;

/**
 * Created by lingdian on 17/9/13.
 * 数据缓存
 */

public class PageInfoImple extends BaseDao<PageInfoDbBean> {
    private static PageInfoImple instance=null;
    private PageInfoImple(){
        super();
    }


    public static PageInfoImple getInstance(){
        if(instance==null){
            instance=new PageInfoImple();
        }
        return instance;
        }

       public List<PageInfoDbBean> getAllDownloadData(){
           try {
               if (isOpen()) {
                   return baseDao.queryForAll();
               } else {
                   return null;
               }
           } catch (Exception e) {
               return null;
           }
       }

    /**
     * 获取下载列表数据
     * @return
     */
    public List<PageInfoDbBean> getDownloadList() {
        try {
            if (isOpen()) {
                QueryBuilder<PageInfoDbBean, Integer> qb = baseDao.queryBuilder();
                qb.where().in("downStatus", DOWNLOAD_STATUS_DOING,DOWNLOAD_STATUS_PAUSE,DOWNLOAD_STATUS_WAITTING);
//                qb.where().eq("downStatus", DOWNLOAD_STATUS_WAITTING).or().eq("downStatus", DOWNLOAD_STATUS_DOING);
                qb.orderBy("date", false);
                return getQuerryInfo(qb);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }
    /**
     *
     * 更改下载的路径
     * @param itemId
     * @param fileUrl
     */
    public void updateDownloadFileUrl(String itemId, String fileUrl){
        try {
            if (isOpen()) {
                UpdateBuilder<PageInfoDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("fileUrl", fileUrl);
                ub.where().eq("itemId", itemId);
                updateData(ub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取已经下载的文件列表
     * @return
     */
    public List<PageInfoDbBean> getDownloadedList() {
        try {
            if (isOpen()) {
                QueryBuilder<PageInfoDbBean, Integer> qb = baseDao.queryBuilder();
                qb.where().eq("downStatus", DOWNLOAD_STATUS_DONE);
                qb.orderBy("date", false);
                return getQuerryInfo(qb);
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public void updateDownloadPlayerStatus(PageInfoDbBean info){
        if(info==null){
            return;
        }
        try {
            if (isOpen()) {
                UpdateBuilder<PageInfoDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("currentTime", info.currentTime);
                ub.where().eq("itemId", info.itemId);
                updateData(ub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  更新总共的时间
     * @param itemId
     * @param totalTime
     */
    public void updateDownloadDuring(String itemId,int totalTime){
        try {
            if (isOpen()) {
                UpdateBuilder<PageInfoDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("totalTime", totalTime);
                ub.where().eq("itemId", itemId);
                updateData(ub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * @param info
     */
    public void updateDownloadStatus(PageInfoDbBean info){
        try {
            if (isOpen()) {
                UpdateBuilder<PageInfoDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("downStatus", info.downStatus);
                ub.where().eq("itemId", info.itemId);
                updateData(ub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
       public void inserPageDownloadData(List<PageInfoDbBean> list){
           try {
               if (isOpen()) {
                   for (PageInfoDbBean info : list) {
                       baseDao.createOrUpdate(info);
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
       }




    @Override
    public void destory() {
        instance = null;
    }
}
