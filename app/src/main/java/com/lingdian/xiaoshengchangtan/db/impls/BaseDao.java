package com.lingdian.xiaoshengchangtan.db.impls;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.lingdian.xiaoshengchangtan.db.DatabaseHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

/**
 * $desc$
 *
 * @author xuhuawei
 * @time $date$ $time$
 */
public abstract class BaseDao<T> {
    protected Dao<T, Integer> baseDao;
    protected DatabaseHelper helper;

    protected BaseDao() {
        try {
            Type t = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) t).getActualTypeArguments();
            Class<T> cls = (Class<T>) params[0];

            helper = DatabaseHelper.getHelper();
            baseDao = helper.getDao(cls);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected boolean isOpen() {
        return helper == null ? false : helper.isOpen();
    }

    public int add(T bean) {
        try {
            return baseDao.create(bean);
        } catch (SQLException e) {
            return -1;
        }
    }


    public List<T> getQuerryInfo(QueryBuilder<T, Integer> qb) {
        try {
//            QueryBuilder<T, Integer> qb = userDao.queryBuilder();
//            qb.where().eq("username", username);
            PreparedQuery<T> pq = qb.prepare();
            return baseDao.query(pq);
        } catch (SQLException e) {
            return null;
        }
    }

    public T getSingleInstance(List<T> arrayList){
        if(arrayList!=null){
            if(arrayList.size()>0){
                return arrayList.get(0);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public int deleteData(DeleteBuilder<T, Integer> pd) {
        try {
//         DeleteBuilder<T,Integer> pd =baseDao.deleteBuilder();
//         pd.where().eq()
            PreparedDelete<T> pq = pd.prepare();
            return baseDao.delete(pq);
        } catch (SQLException e) {
            return -1;
        }
    }


    public int updateData(UpdateBuilder<T, Integer> ub) {
        try {
//        UpdateBuilder<T,Integer> ub=baseDao.updateBuilder();
//        ub.where().eq()
            PreparedUpdate<T> pu = ub.prepare();
            return baseDao.update(pu);
        } catch (SQLException e) {
            return -1;
        }
    }
    public abstract  void destory();
}
