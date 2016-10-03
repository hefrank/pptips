package com.ppamy.pptips.datamgr;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ppamy.pptips.AppEnv;
import com.ppamy.pptips.MainApplication;
import com.ppamy.pptips.dbs.tips_dao.DaoMaster;
import com.ppamy.pptips.dbs.tips_dao.DaoSession;
import com.ppamy.pptips.dbs.tips_dao.Tips;
import com.ppamy.pptips.dbs.tips_dao.TipsDao;
import com.ppamy.pptips.dbs.tips_dao.TipsDaoOpenHelper;
import com.ppamy.pptips.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.query.Query;

public class TipsDataManager {
    private static final String TAG = "TipsDataManager";

    private final static int MAX_COUNT = 10;

    private static TipsDataManager instance = null;

    private static final byte[] LOCK = new byte[0];

    private static final byte[] DB_LOCK = new byte[0];

    private final SQLiteDatabase db;

    private final DaoMaster daoMaster;

    private final DaoSession daoSession;

    private final TipsDao mTipsDao;

    private TipsDataManager() {
        // 使用greenDao
        SQLiteOpenHelper helper = new TipsDaoOpenHelper(MainApplication.getApp(), AppEnv.DB_TIPS_NAME, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        // TODO: 不保留每次的query结果，有优化空间
        daoSession = daoMaster.newSession(IdentityScopeType.None);
        mTipsDao = daoSession.getTipsDao();
    }

    public static TipsDataManager getInstance() {
        if (null != instance) {
            return instance;
        } else {
            synchronized (LOCK) {
                if (null == instance) {
                    instance = new TipsDataManager();
                }
            }
            return instance;
        }
    }

    public long addOrUpdateItem(Tips tips) {
        long id = mTipsDao.insertOrReplace(tips);

        return id;
    }
    public void delItem(long id){
        mTipsDao.deleteByKey(id);
    }

    public Tips getTipsItemById(long id ){
        Query<Tips> query = mTipsDao.queryBuilder().where(TipsDao.Properties.Id.eq(id)).build();
        query.forCurrentThread();
        return query.unique();
    }

    public List<Tips> loadAllItems() {
        List<Tips> all = mTipsDao.loadAll();
//        Collections.sort(all, new Comparator<Tips>() {
//
//            @Override
//            public int compare(Tips lhs, Tips rhs) {
//                if (rhs.getDate() > lhs.getDate()){
//                    return 1;
//                }else if (rhs.getDate() < lhs.getDate()){
//                    return -1;
//                }else{
//                    return 0;
//                }
//            }
//        });

        return all;

    }

    public long getTipsCount() {
        return mTipsDao.count();
    }

    /**
     * 获取所有记录笔记的日期
     */
    public ArrayList<String> getAllDays() {
        ArrayList<String> days = new ArrayList<String>();

        List<Tips> all = mTipsDao.loadAll();
        for (Tips tips : all) {
            long tmp = tips.getDate();
            String sdate = Utils.getFormatedDate(MainApplication.getApp(),tmp);
//            Log.e("kkk", "day is sdate is " + sdate);
            days.add(sdate);
        }
        return days;
    }
    /**
     *
     * */
    public Map<String,Integer> getDateCount(){
        ArrayList<String> days = getAllDays();
        Map<String,Integer> dc = new HashMap<String,Integer>();
//        ArrayList<Integer> dc = new ArrayList<Integer>();

        for (String dd:days){
//            Log.e("kkk","day is "+dd);
            Integer cc = dc.get(dd);
            if (cc != null){
                cc++;
                dc.put(dd,cc);
            }else{
                dc.put(dd,1);
            }

        }
        return dc;
    }

    /**max 10条*/
    public ArrayList<String> getMarkDate(){
        ArrayList<String> days = new ArrayList<String>();
        List<Tips> all = mTipsDao.loadAll();

        for (Tips tips : all) {
            Long tmp = tips.getDate();
            String sdate = Utils.getFormatedDate(MainApplication.getApp(),tmp);
//            Log.e("kkk", "xx day is sdate is tmp is "+tmp.longValue() );
            if(days.contains(sdate)) {
//                Log.e("kkk", "xx day is sdate is tmpSet.contains "+tmp.longValue() );
            }else{
//                Log.e("kkk", "xx day is sdate is " + sdate);
                days.add(sdate);
            }
        }
        Collections.reverse(days);
        return days;
    }

    //新整理时间

    public List<Tips> getAllDaysAfterTime(long time) {
        Query<Tips> query = mTipsDao.queryBuilder().where(TipsDao.Properties.Date.gt(time)).build();
        query.forCurrentThread();
        return query.list();
    }

    public void destory(){
        instance = null;
    }
}
