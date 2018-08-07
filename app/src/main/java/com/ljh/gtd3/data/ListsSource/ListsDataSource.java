package com.ljh.gtd3.data.ListsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public interface ListsDataSource {

    interface GetListsCallBack{
        void onListsLoaded(java.util.List<List> lists, String message);
        void onListsFail(String message);
    }

    interface GetListCallBack{
        void onListLoaded(List list, String message);
        void onListFail(String message);
    }

    interface SendRequestCallBack{
        void onRequestSuccess(String message);
        void onRequestFail(String message);
    }

    void GetList(@NonNull String listId, @NonNull GetListCallBack callBack);

    void GetLists(@NonNull String userId, @NonNull GetListsCallBack callBack);

    void GetListsByListGroupId(@NonNull String userId, @NonNull String listGroupId, @NonNull GetListsCallBack callBack);

    void deleteList(@NonNull String listId, @NonNull SendRequestCallBack callBack);

    void deleteLists(@NonNull String userId, @NonNull SendRequestCallBack callBack);

    void updateList(@NonNull List list);  //没有回调，直接在ui中修改

    void addList(@NonNull List list); //没有回调，直接在ui中修改
}
