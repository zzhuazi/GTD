package com.ljh.gtd3.data.StuffsSource;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class StuffsLocalDataSource implements StuffsDataSource {

    private static final String TAG = StuffsLocalDataSource.class.getSimpleName();
    private static StuffsLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    //防止直接被实例化
    private StuffsLocalDataSource(@NonNull AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
    }

    public static StuffsLocalDataSource getInstance(@NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (StuffsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StuffsLocalDataSource(appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getStuff(@NonNull final String stuffId, @NonNull final GetStuffCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Stuff> stuffs = DataSupport.where("stuffId = ? ", stuffId).order("startTime asc").find(Stuff.class);
                if (stuffs.isEmpty()) {
                    callBack.onStuffFail("没有该材料");
                } else {
                    callBack.onStuffLoaded(stuffs.get(0), "获取stuff成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getAllStuffs(@NonNull final String userId, @NonNull final GetStuffsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Stuff> stuffs = DataSupport.where("userId = ?", userId).find(Stuff.class);
                if (stuffs.isEmpty()) {
                    callBack.onStuffsFail("当前用户下没有材料");
                } else {
                    callBack.onStuffsLoaded(stuffs, "获取所有材料成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getStuffsByListId(@NonNull final String userId, @NonNull final String listId, @NonNull final GetStuffsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Stuff> stuffs = DataSupport.where("userId = ? and listId = ?", userId, listId).find(Stuff.class);
                if (stuffs.isEmpty()) {
                    Log.d(TAG, "run: stuffsbyListId is null? " + stuffs.isEmpty());
                    callBack.onStuffsFail("当前用户下没有材料");
                } else {
                    Log.d(TAG, "run: stuffsbyListId " + stuffs.size());
                    callBack.onStuffsLoaded(stuffs, "获取所有材料成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getStuffsByStartDate(@NonNull final String userId, @NonNull final String startDate, @NonNull final GetStuffsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Stuff> stuffs = DataSupport.where("userId = ? and startTime like ?", userId, startDate.substring(0,10)+"%").find(Stuff.class);
                if (stuffs.isEmpty()) {
                    callBack.onStuffsFail("当前用户下没有材料");
                } else {
                    callBack.onStuffsLoaded(stuffs, "获取所有材料成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteStuff(@NonNull final String stuffId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(Stuff.class, "stuffId = ?", stuffId);
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
    public void deleteStuffs(@NonNull final String userId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(Stuff.class, "userId = ?", userId);
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
    public void deleteStuffsByListId(@NonNull final String listId, @NonNull final SendRequestCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(Stuff.class, "listId = ?", listId);
                if (i == 0) {
                    callback.onRequestFail("删除失败");
                } else {
                    callback.onRequestSuccess("删除成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateStuff(@NonNull final Stuff stuff) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                stuff.updateAll("stuffId = ?", stuff.getStuffId());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addStuff(@NonNull final Stuff stuff) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: stuff id" + stuff.getStuffId());
                getStuff(stuff.getStuffId(), new GetStuffCallBack() {
                    @Override
                    public void onStuffLoaded(Stuff stuff, String message) {

                    }

                    @Override
                    public void onStuffFail(String message) {
                        stuff.save();
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
