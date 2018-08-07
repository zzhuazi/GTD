package com.ljh.gtd3.data.ListGroupsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.ListGroup;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class ListGroupsLocalDataSource implements ListGroupsDataSource{
    private static ListGroupsLocalDataSource INSTANCE;
    private AppExecutors mAppExecutors;

    private ListGroupsLocalDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static ListGroupsLocalDataSource getInstance(AppExecutors appExecutors){
        if(INSTANCE == null) {
            INSTANCE = new ListGroupsLocalDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getListGroup(@NonNull final String listGroupId, @NonNull final GetListGroupCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<ListGroup> listGroups = DataSupport.where("listGroupId = ?", listGroupId).find(ListGroup.class);
                if(listGroups.isEmpty()) {
                    callBack.onListGroupFail("没有清单组");
                }else {
                    callBack.onListGroupLoaded(listGroups.get(0), "success");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getListGroups(@NonNull final String userId, @NonNull final GetListGroupsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<ListGroup> listGroups = DataSupport.where("userId = ?", userId).find(ListGroup.class);
                if(listGroups.isEmpty()) {
                    callBack.onListGroupsFail("没有清单组");
                }else {
                    callBack.onListGroupsLoaded(listGroups, "success");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addListGroup(@NonNull final ListGroup listGroup) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getListGroup(listGroup.getListGroupId(), new GetListGroupCallBack() {
                    @Override
                    public void onListGroupLoaded(ListGroup listGroup, String message) {

                    }

                    @Override
                    public void onListGroupFail(String message) {
                        listGroup.save();
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateListGroup(@NonNull final ListGroup listGroup) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                listGroup.updateAll("listGroupId = ?", listGroup.getListGroupId());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteListGroup(@NonNull final String listGroupId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(ListGroup.class, "listGroupId = ?", listGroupId);
                if(i == 0) {
                    callBack.onRequestFail("删除失败");
                }else {
                    callBack.onRequestSuccess("删除成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
