package com.ljh.gtd3.data.ListsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.LitePal;

/**
 * Created by Administrator on 2018/3/9.
 */

public class ListsLocalDataSource implements ListsDataSource {
    public static final String TAG = ListsLocalDataSource.class.getSimpleName();
    private static ListsLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    //防止被直接实例化
    private ListsLocalDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static ListsLocalDataSource getInstance(@NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            INSTANCE = new ListsLocalDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void GetList(@NonNull final int listId, @NonNull final GetListCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List list = LitePal.find(List.class, listId);
                if (list == null) {
                    callBack.onListFail("小二丢失了该清单");
                } else {
                    callBack.onListLoaded(list, "小二为你呈现清单");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void GetLists(@NonNull final GetListsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                java.util.List<List> lists = LitePal.findAll(List.class);
                if (lists.isEmpty()) {
                    callBack.onListsFail("没有清单");
                } else {
                    callBack.onListsLoaded(lists, "获取清单成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }


    @Override
    public void deleteList(@NonNull final int listId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LitePal.delete(List.class, listId);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllLists() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LitePal.deleteAll(List.class);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateList(@NonNull final List list) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                list.update(list.getId());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addList(@NonNull final List list) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GetList(list.getId(), new GetListCallBack() {
                    @Override
                    public void onListLoaded(List list, String message) {
                        list.update(list.getId());
                    }

                    @Override
                    public void onListFail(String message) {
                        list.save();
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
