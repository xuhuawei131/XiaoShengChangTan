package com.lingdian.xiaoshengchangtan.db.impls;

import com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean;

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

    @Override
    public void destory() {

    }
}
