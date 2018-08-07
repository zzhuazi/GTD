package com.ljh.gtd3.addList;

import android.util.Log;

import com.ljh.gtd3.data.ListGroupsSource.ListGroupsDataSource;
import com.ljh.gtd3.data.ListGroupsSource.ListGroupsRepository;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.ListGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/17.
 */

public class AddListPresenter implements AddListContract.Presenter{
    public static final String TAG = AddListPresenter.class.getSimpleName();

    private final ListsRepository mListsRepository;

    private final ListGroupsRepository mListGroupsRepository;

    private final AddListContract.View mAddListView;

    private String mUserId;

    private String mListId;

    public AddListPresenter(ListsRepository mListsRepository, ListGroupsRepository mListGroupsRepository, AddListContract.View mAddListView, String mUserId, String mListId) {
        this.mListsRepository = mListsRepository;
        this.mListGroupsRepository = mListGroupsRepository;
        this.mAddListView = mAddListView;
        this.mUserId = mUserId;
        this.mListId = mListId;
        mAddListView.setPresenter(this);
    }

    @Override
    public void start() {
        if(mListId != null){
            mListsRepository.GetList(mListId, new ListsDataSource.GetListCallBack() {
                @Override
                public void onListLoaded(final List list, String message) {
                    if(list.getListGroupId() != null) {
                        mListGroupsRepository.getListGroup(list.getListGroupId(), new ListGroupsDataSource.GetListGroupCallBack() {
                            @Override
                            public void onListGroupLoaded(ListGroup listGroup, String message) {
                                mAddListView.showList(list, listGroup);
                            }

                            @Override
                            public void onListGroupFail(String message) {

                            }
                        });
                    }else {
                        mAddListView.showList(list, null);
                    }

                }

                @Override
                public void onListFail(String message) {

                }
            });
        }
    }

    @Override
    public void showSelectListGroups() {
        //显示listGroups的信息
        mListGroupsRepository.getListGroups(mUserId, new ListGroupsDataSource.GetListGroupsCallBack() {
            @Override
            public void onListGroupsLoaded(java.util.List<ListGroup> listGroups, String message) {
                if(listGroups.size() > 1) {
                    mAddListView.showSelectListGroups(listGroups);
                }else{
                    mAddListView.showSelectListGroups(null);
                }
            }

            @Override
            public void onListGroupsFail(String message) {
                mAddListView.showSelectListGroups(null);
            }
        });
    }

    @Override
    public void addList(List list) {
        list.setUserId(mUserId);
        mListsRepository.addList(list);
    }

    @Override
    public void showListGroup() {
        mAddListView.showListGroups();
    }

    @Override
    public void addListGroup(ListGroup listGroup) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        listGroup.setUserId(mUserId);
        listGroup.setGmtCreate(simpleDateFormat.format(new Date()));
        listGroup.setGmtModified(simpleDateFormat.format(new Date()));
        mListGroupsRepository.addListGroup(listGroup);
    }

    @Override
    public void updateList(List list) {
        list.setUserId(mUserId);
        mListsRepository.updateList(list);

    }
}
