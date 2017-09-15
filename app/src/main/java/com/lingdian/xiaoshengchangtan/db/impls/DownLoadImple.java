package com.lingdian.xiaoshengchangtan.db.impls;

import com.j256.ormlite.stmt.QueryBuilder;
import com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean;

import java.sql.SQLException;
import java.util.List;

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



       public void inserDownloadData(List<DownLoadDbBean> list){
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
