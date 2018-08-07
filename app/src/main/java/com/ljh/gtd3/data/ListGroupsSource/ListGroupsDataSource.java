package com.ljh.gtd3.data.ListGroupsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.ListGroup;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public interface ListGroupsDataSource {

    interface GetListGroupsCallBack{
        void onListGroupsLoaded(List<ListGroup> listGroups, String message);
        void onListGroupsFail(String message);
    }

    interface GetListGroupCallBack{
        void onListGroupLoaded(ListGroup listGroup, String message);
        void onListGroupFail(String message);
    }

    interface SendRequestCallBack{
        void onRequestSuccess(String message);
        void onRequestFail(String message);
    }

    void getListGroup(@NonNull String listGroupId, @NonNull GetListGroupCallBack callBack);

    void getListGroups(@NonNull String userId, @NonNull GetListGroupsCallBack callBack);

    void addListGroup(@NonNull ListGroup listGroup);

    void updateListGroup(@NonNull ListGroup listGroup);

    void deleteListGroup(@NonNull String listGroupId, @NonNull SendRequestCallBack callBack);
}
