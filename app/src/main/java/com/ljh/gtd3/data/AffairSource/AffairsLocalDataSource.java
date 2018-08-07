package com.ljh.gtd3.data.AffairSource;

import android.app.Application;
import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class AffairsLocalDataSource implements AffairsDataSource {
    private static AffairsLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    private AffairsLocalDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static AffairsLocalDataSource getInstance(@NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            INSTANCE = new AffairsLocalDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }

    @Override
    public void getAffair(@NonNull final String affairId, @NonNull final GetAffairCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Affair> affairs = DataSupport.where("affairId = ?", affairId).find(Affair.class);
                if (affairs.size() == 0) {
                    callBack.onAffairFail("没有事务");
                } else {
                    callBack.onAffairLoaded(affairs.get(0), "success");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getAffairs(@NonNull final String stuffId, @NonNull final GetAffairsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Affair> affairs = DataSupport.where("stuffId = ?", stuffId).find(Affair.class);
                if (affairs.size() == 0) {
                    callBack.onAffairsFail("该材料下没有事务");
                } else {
                    callBack.onAffairsLoaded(affairs, "success");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addAffair(@NonNull final Affair affair) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getAffair(affair.getAffairId(), new GetAffairCallBack() {
                    @Override
                    public void onAffairLoaded(Affair affair, String message) {

                    }

                    @Override
                    public void onAffairFail(String message) {
                        affair.save();
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateAffair(@NonNull final Affair affair) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                affair.updateAll("affairId = ?", affair.getAffairId());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAffair(@NonNull final String affairId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(Affair.class, "affairId = ?", affairId);
                if (i == 0) {
                    callBack.onRequestFail("删除失败");
                } else {
                    callBack.onRequestSuccess("删除成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAffairs(@NonNull final String stuffId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(Affair.class, "stuffId = ?", stuffId);
                if (i == 0) {
                    callBack.onRequestFail("删除失败");
                } else {
                    callBack.onRequestSuccess("删除成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
