package com.ljh.gtd3.data.ListsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.List;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/9.
 */

public class ListsRepository implements ListsDataSource {
    public static final String TAG = ListsRepository.class.getSimpleName();
    private static ListsRepository INSTANCE = null;

    private final ListsDataSource mListsLocalDataSource;


    private ListsRepository(ListsDataSource mListsLocalDataSource) {
        this.mListsLocalDataSource = mListsLocalDataSource;
    }

    public static ListsRepository getInstance(@NonNull ListsDataSource listsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ListsRepository(listsLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    //在本地获取
    @Override
    public void GetList(@NonNull int listId, @NonNull final GetListCallBack callBack) {

        //否则在本地数据库中获取
        mListsLocalDataSource.GetList(listId, new GetListCallBack() {
            @Override
            public void onListLoaded(List list, String message) {
                callBack.onListLoaded(list, message);
            }

            @Override
            public void onListFail(String message) {
                callBack.onListFail(message);
            }
        });
    }



    //在本地获取
    @Override
    public void GetLists(@NonNull final GetListsCallBack callBack) {
        //到本地数据库中查询数据
        mListsLocalDataSource.GetLists(new GetListsCallBack() {
            @Override
            public void onListsLoaded(java.util.List<List> lists, String message) {
                callBack.onListsLoaded(lists, message);
            }

            @Override
            public void onListsFail(String message) {
              callBack.onListsFail(message);
            }
        });
    }

    private void refreshLocalDataSource(String userId, final java.util.List<List> lists) {
        for (final List list : lists) {
            mListsLocalDataSource.GetList(list.getId(), new GetListCallBack() {
                @Override
                public void onListLoaded(List list, String message) {
                    mListsLocalDataSource.updateList(list);
                }

                @Override
                public void onListFail(String message) {
                    mListsLocalDataSource.addList(list);
                }
            });
        }
    }

    //在缓存、本地数据库、服务器中删除
    @Override
    public void deleteList(@NonNull final int listId) {
        //先从本地数据库删除、再到服务器中删除
        mListsLocalDataSource.deleteList(listId);
    }

    //从缓存、本地删除
    @Override
    public void deleteAllLists() {
        mListsLocalDataSource.deleteAllLists();
    }

    @Override
    public void updateList(@NonNull final List list) {
        mListsLocalDataSource.updateList(list);
    }

    @Override
    public void addList(@NonNull List list) {
        mListsLocalDataSource.addList(list);
//
    }

    public void updateTasksNum(int listId) {
        mListsLocalDataSource.GetList(listId, new GetListCallBack() {
            @Override
            public void onListLoaded(List list, String message) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                list.setTasks(list.getTasks() + 1);
                list.setGmtModified(simpleDateFormat.format(new Date()));
                mListsLocalDataSource.updateList(list);
            }

            @Override
            public void onListFail(String message) {

            }
        });
    }

    public void subtractTasksNum(int listId) {
        mListsLocalDataSource.GetList(listId, new GetListCallBack() {
            @Override
            public void onListLoaded(List list, String message) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                list.setTasks(list.getTasks() - 1);
                list.setGmtModified(simpleDateFormat.format(new Date()));
                mListsLocalDataSource.updateList(list);
            }

            @Override
            public void onListFail(String message) {

            }
        });
    }
}
