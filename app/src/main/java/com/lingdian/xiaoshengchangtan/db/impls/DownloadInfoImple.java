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
 * 下载的进度管理
 */

public class DownloadInfoImple extends BaseDao<DownloadInfoDbBean> {
    private static DownloadInfoImple instance = null;

    private DownloadInfoImple() {
        super();
    }


    public static DownloadInfoImple getInstance() {
        if (instance == null) {
            instance = new DownloadInfoImple();
        }
        return instance;
    }

    /**
     * 获取所有的下载数据
     *
     * @return
     */
    public List<DownloadInfoDbBean> getAllDownloadData() {
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
    public DownloadInfoDbBean getDownloadList(String itemId) {
        try {
            if (isOpen()) {
                QueryBuilder<DownloadInfoDbBean, Integer> qb = baseDao.queryBuilder();
                qb.where().eq("itemId", itemId);
                List<DownloadInfoDbBean> list = getQuerryInfo(qb);
                if (list != null && list.size() > 0) {
                    return list.get(0);
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }

    /**
     * 获取已经下载的文件列表
     *
     * @return
     */
    public List<DownloadInfoDbBean> getDownloadedList() {
        try {
            if (isOpen()) {
                QueryBuilder<DownloadInfoDbBean, Integer> qb = baseDao.queryBuilder();
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
     * 更新下载的状态
     *
     * @param itemId
     * @param downStatus
     */
    public void updateDownloadStatus(String itemId, int downStatus) {
        try {
            if (isOpen()) {
                UpdateBuilder<DownloadInfoDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("downStatus", downStatus);
                ub.where().eq("itemId", itemId);
                updateData(ub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新总共的时间
     *
     * @param itemId
     * @param totalSize
     */
    public synchronized void updateDownloadTotalSize(String itemId, long totalSize) {
        try {
            if (isOpen()) {
                UpdateBuilder<DownloadInfoDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("totalSize", totalSize);
                ub.where().eq("itemId", itemId);
                updateData(ub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新总共的时间
     *
     * @param itemId
     * @param fileDownSize
     */
    public synchronized void updateDownloadSize(String itemId, long fileDownSize) {
        try {
            if (isOpen()) {
                UpdateBuilder<DownloadInfoDbBean, Integer> ub = baseDao.updateBuilder();
                ub.updateColumnValue("fileDownSize", fileDownSize);
                ub.where().eq("itemId", itemId);
                updateData(ub);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新插入下载状态
     * @param list
     */
    public void inserPageDownloadData(List<DownloadInfoDbBean> list) {
        try {
            if (isOpen()) {
                for (DownloadInfoDbBean info : list) {
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
