package com.lingdian.xiaoshengchangtan.db.impls;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.lingdian.xiaoshengchangtan.config.SingleCacheData;
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
    private static PageInfoImple instance = null;

    private PageInfoImple() {
        super();
    }


    public static PageInfoImple getInstance() {
        if (instance == null) {
            instance = new PageInfoImple();
        }
        return instance;
    }

    /**
     * 获取所有页面的缓存
     *
     * @return
     */
    public List<PageInfoDbBean> getAllPageData() {
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
     *
     * @return
     */
    public List<PageInfoDbBean> getDownloadList() {
        try {
            if (isOpen()) {
                QueryBuilder<PageInfoDbBean, Integer> qb = baseDao.queryBuilder();
//                qb.where().in("downStatus", DOWNLOAD_STATUS_DOING,DOWNLOAD_STATUS_PAUSE,DOWNLOAD_STATUS_WAITTING);
//                qb.where().eq("downStatus", DOWNLOAD_STATUS_WAITTING).or().eq("downStatus", DOWNLOAD_STATUS_DOING);
                qb.orderBy("date", false);
                return getQuerryInfo(qb);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取已经下载的文件列表
     *
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

    /**
     * 获取指定的某个数据
     *
     * @param itemId
     * @return
     */
    public PageInfoDbBean getPageItemInfo(String itemId) {
        try {
            if (isOpen()) {
                QueryBuilder<PageInfoDbBean, Integer> qb = baseDao.queryBuilder();
                qb.where().eq("itemId", itemId);
                List<PageInfoDbBean> list = getQuerryInfo(qb);
                if (list != null && list.size() > 0) {
                    return list.get(0);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * 更新播放的进度
     */
    public void updatePlayProgress() {
        PageInfoDbBean currentBean = SingleCacheData.getInstance().getCurrentPlayBean();
        try {
            PageInfoDbBean bean=getPageItemInfo(currentBean.itemId);
            if (bean!=null){
                if (isOpen()) {
                    UpdateBuilder<PageInfoDbBean, Integer> ub = baseDao.updateBuilder();
                    ub.updateColumnValue("currentTime", currentBean.currentTime);
                    ub.where().eq("itemId", currentBean.itemId);
                    updateData(ub);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 批量插入下载缓存的数据
     * @param info
     */
    public void inserPageDownloadData(PageInfoDbBean info) {
        try {
            if (isOpen()) {
                    baseDao.createOrUpdate(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量插入下载缓存的数据列表
     * @param list
     */
    public void inserPageDownloadDataList(List<PageInfoDbBean> list) {
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
