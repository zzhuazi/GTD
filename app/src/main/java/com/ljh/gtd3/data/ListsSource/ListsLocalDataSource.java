package com.ljh.gtd3.data.ListsSource;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/3/9.
 */

public class ListsLocalDataSource implements  ListsDataSource{
    public static final String TAG = ListsLocalDataSource.class.getSimpleName();
    private static ListsLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    //防止被直接实例化
    private ListsLocalDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static ListsLocalDataSource getInstance(@NonNull AppExecutors appExecutors){
        if(INSTANCE == null) {
            INSTANCE = new ListsLocalDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void GetList(@NonNull final String listId, @NonNull final GetListCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                java.util.List<List> lists = DataSupport.where("listId = ? ", listId).find(List.class);
                if(lists.isEmpty()){
                    callBack.onListFail("没有该材料");
                }else {
                    callBack.onListLoaded(lists.get(0), "获取list成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void GetLists(@NonNull final String userId, @NonNull final GetListsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                java.util.List<List> lists = DataSupport.where("userId = ?", userId).find(List.class);
                if(lists.isEmpty()) {
                    callBack.onListsFail("当前用户下没有清单");
                }else {
                    callBack.onListsLoaded(lists, "获取清单成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void GetListsByListGroupId(@NonNull final String userId, @NonNull final String listGroupId, @NonNull final GetListsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                java.util.List<List> lists = DataSupport.where("userId = ? and listGroupId = ?", userId, listGroupId).find(List.class);
                if(lists.isEmpty()) {
                    callBack.onListsFail("该文件夹下没有清单");
                }else {
                    callBack.onListsLoaded(lists, "获取清单成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteList(@NonNull final String listId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(List.class, "listId = ?", listId);
                if(i == 0) {
                    callBack.onRequestFail("删除失败");
                }else {
                    callBack.onRequestSuccess("删除成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteLists(@NonNull final String userId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(List.class, "userId = ?", userId);
                if(i == 0) {
                    callBack.onRequestFail("删除失败");
                }else {
                    callBack.onRequestSuccess("删除成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateList(@NonNull final List list) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                list.updateAll("listId = ?", list.getListId());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addList(@NonNull final List list) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GetList(list.getListId(), new GetListCallBack() {
                    @Override
                    public void onListLoaded(List list, String message) {

                    }

                    @Override
                    public void onListFail(String message) {
                        Log.d(TAG, "onListFail: 不存在该List, 添加List");
                        list.save();
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
