package com.lingdian.xiaoshengchangtan.db.impls;

import android.text.TextUtils;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;

import java.sql.SQLException;
import java.util.List;

import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DOING;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_DONE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_PAUSE;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_WAITTING;

/**
 * Created by lingdian on 17/9/13.
 */

public class DownLoadImple extends BaseDao<DownLoadDbBean> {
    private static  DownLoadImple instance=null;
    private DownLoadImple(){
        super();
    }


    public static DownLoadImple getInstance(){
        if(instance==null){
            instance=new DownLoadImple();
        }
        return instance;
        }

       public List<DownLoadDbBean> getAllDownloadData(){
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
    public List<DownLoadDbBean> getDownloadList() {
        try {
            if (isOpen()) {
                QueryBuilder<DownLoadDbBean, Integer> qb = baseDao.queryBuilder();
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
     * 获取已经下载的文件列表
     * @return
     */
    public List<DownLoadDbBean> getDownloadedList() {
        try {
            if (isOpen()) {
                QueryBuilder<DownLoadDbBean, Integer> qb = baseDao.queryBuilder();
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

    public void updateDownloadPlayerStatus(DownLoadDbBean info){
        try {
            if (isOpen()) {
                UpdateBuilder<DownLoadDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("currentTime", info.currentTime);
                ub.where().eq("title", info.title);
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
    public void updateDownloadStatus(DownLoadDbBean info){
        try {
            if (isOpen()) {
                UpdateBuilder<DownLoadDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("downStatus", info.downStatus);
                ub.where().eq("title", info.title);
                updateData(ub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
       public void inserPageDownloadData(List<DownLoadDbBean> list){
           try {
               if (isOpen()) {
                   for (DownLoadDbBean info : list) {
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
