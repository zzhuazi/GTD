package com.ljh.gtd3.listGroup;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.ListGroupsSource.ListGroupsDataSource;
import com.ljh.gtd3.data.ListGroupsSource.ListGroupsRepository;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.StuffsSource.StuffsDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.ListGroup;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/16.
 */
public class ListGroupPresenter implements ListGroupContract.Presenter {
    public static final String TAG = ListGroupPresenter.class.getSimpleName();
    private final UsersRepository mUsersRepository;
    private final ListGroupsRepository mListGroupsRepository;
    private final ListsRepository mListsRepository;
    private final StuffsRepository mStuffsRepository;
    private final ListGroupContract.View mListGroupView;
    private String mUserId;

    public ListGroupPresenter(UsersRepository mUsersRepository, ListGroupsRepository mListGroupsRepository, ListsRepository mListsRepository, StuffsRepository mStuffsRepository, ListGroupContract.View mListGroupView, String mUserId) {
        this.mUsersRepository = mUsersRepository;
        this.mListGroupsRepository = mListGroupsRepository;
        this.mListsRepository = mListsRepository;
        this.mStuffsRepository = mStuffsRepository;
        this.mListGroupView = mListGroupView;
        this.mUserId = mUserId;
        this.mListGroupView.setPresenter(this);
    }

    @Override
    public void start() {
        loadLists(false);
    }

    @Override
    public void showAddStuff() {
        mListGroupView.showAddStuff();
    }

    @Override
    public void addListGroup(ListGroup listGroup) {
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            listGroup.setUserId(mUserId);
            listGroup.setGmtCreate(simpleDateFormat.format(new Date()));
            listGroup.setGmtModified(simpleDateFormat.format(new Date()));
            mListGroupsRepository.addListGroup(listGroup);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void addStuff(final Stuff stuff) {
        mListsRepository.GetLists(mUserId, new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(java.util.List<List> lists, String message) {
                Log.d(TAG, "onListsLoaded: list.size():" + lists.size());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                stuff.setStuffId(UUID.randomUUID().toString());
                stuff.setPriority(0);
                stuff.setUserId(mUserId);
                stuff.setListId(lists.get(0).getListId());
                stuff.setGmtCreate(simpleDateFormat.format(new Date()));
                stuff.setGmtModified(simpleDateFormat.format(new Date()));
                stuff.setFinished(false);
                mStuffsRepository.addStuff(stuff);
//                mListsRepository.updateStuffsNum(stuff.getListId());
            }

            @Override
            public void onListsFail(String message) {

            }
        });
    }

    @Override
    public void loadLists(boolean forceUpdate) {
        loadLists(forceUpdate, true);
        loadUser(mUserId);
    }

    /**
     *
     * @param forceUpdate 是否强制更新（是否在服务器中请求数据）
     * @param showLoadingUI  是否显示ui
     */
    private void loadLists(boolean forceUpdate, boolean showLoadingUI) {
        if(showLoadingUI) {
            mListGroupView.setLoadingIndicator(true);
        }
        if(forceUpdate) {
            mListGroupsRepository.getListGroupsFromRemoteDateSource(mUserId, new ListGroupsDataSource.GetListGroupsCallBack() {
                @Override
                public void onListGroupsLoaded(java.util.List<ListGroup> listGroups, String message) {
                    Log.d(TAG, "onListGroupsLoaded: success" + listGroups.size());
                    if(listGroups.size() == 0) {
                        dealWithListGroupEmple();
                    }else {
                        //如果该用户下有listGroup，则将对应的List分为一个组，否则设置为其他listGroup
                        dealWithListGroupNonEmple(listGroups);
                    }
                }

                @Override
                public void onListGroupsFail(String message) {
                    dealWithListGroupEmple();
                }
            });
        }else {
            mListGroupsRepository.getListGroups(mUserId, new ListGroupsDataSource.GetListGroupsCallBack() {
                @Override
                public void onListGroupsLoaded(final java.util.List<ListGroup> listGroups, String message) {
                    Log.d(TAG, "onListGroupsLoaded: success" + listGroups.size());
                    if(listGroups.size() == 0) {
                        dealWithListGroupEmple();
                    }else {
                        //如果该用户下有listGroup，则将对应的List分为一个组，否则设置为其他listGroup
                        dealWithListGroupNonEmple(listGroups);
                    }
                }

                @Override
                public void onListGroupsFail(String message) {
                    Log.d(TAG, "onListGroupsFail: false " );
                    dealWithListGroupEmple();
                }
            });
        }
    }

    private void dealWithListGroupNonEmple(final java.util.List<ListGroup> listGroups) {
        Log.d(TAG, "dealWithListGroupNonEmple: ");
        mListsRepository.GetLists(mUserId, new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(java.util.List<List> lists, String message) {
                try{
                    if(lists.size() != 0) {
                        java.util.List<java.util.List<List>> listList = new ArrayList<>();
                        java.util.List<List> unnormalList = new ArrayList<>(); //存放没有有listgroup的list
                        for (int i = 0; i< listGroups.size(); i++){   //遍历listgroup ，判断list是否在listgroup中
                            java.util.List<List> normalList = new ArrayList<>(); //存放有listgroup的list
                            for (Iterator iterator = lists.iterator(); iterator.hasNext();){ //遍历所有List
                                List list = (List) iterator.next();
                                if(list.getListGroupId() == null || list.getListGroupId().equals("") || list.getListGroupId().equals("null")) {
                                    //如果该List没有所属的listGroup，则添加在otherListGroup中，并将其在lists中清除
                                    unnormalList.add(list);
                                    iterator.remove();
                                }else if(list.getListGroupId().equals(listGroups.get(i).getListGroupId())) {
                                    normalList.add(list);
                                    Log.d(TAG, "onListsLoaded: normalList.size" + normalList.size());
                                }
                            }
                            listList.add(normalList);
                        }
                        listList.add(unnormalList);  //将没有Listgroup的分组放在最后
                        Log.d(TAG, "onListsLoaded: listList.size" + listList.size());
                        ListGroup otherListGroup = new ListGroup();
                        otherListGroup.setName("其他");
                        listGroups.add(otherListGroup); //将其他listGroup 放在最后
                        if(!mListGroupView.isActive()) {
                            mListGroupView.setLoadingIndicator(false);
                            return;
                        }
                        mListGroupView.setLoadingIndicator(false);
                        mListGroupView.showAllLists(listGroups, listList);
                    }else {
                        mListGroupView.setLoadingIndicator(false);
                        mListGroupView.setLoadingStuffsError();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mListGroupView.setLoadingIndicator(false);
                    mListGroupView.setLoadingStuffsError();
                }
            }

            @Override
            public void onListsFail(String message) {
                mListGroupView.setLoadingIndicator(false);
                mListGroupView.setLoadingStuffsError();
            }
        });
    }

    private void dealWithListGroupEmple() {
        Log.d(TAG, "dealWithListGroupEmple: ");
        mListsRepository.GetLists(mUserId, new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(java.util.List<List> lists, String message) {
                java.util.List<ListGroup> listGroupList = new ArrayList<>();
                ListGroup listGroup = new ListGroup();
                listGroup.setName("其他");
                listGroupList.add(listGroup);
                java.util.List<java.util.List<List>> listList = new ArrayList<>();
                listList.add(lists);
                if(!mListGroupView.isActive()) {
                    mListGroupView.setLoadingIndicator(false);
                    return;
                }
                mListGroupView.setLoadingIndicator(false);
                mListGroupView.showAllLists(listGroupList, listList); //将数据传到fragment中进行显示
            }

            @Override
            public void onListsFail(String message) {
                mListGroupView.setLoadingIndicator(false);
                mListGroupView.setLoadingStuffsError();
            }
        });
    }

    @Override
    public void showListDetail(@NonNull List requestList) {
        mListGroupView.showListDetail(requestList);
    }

    @Override
    public void showUserSetting() {
        mListGroupView.showUserSetting();
    }

    @Override
    public void startVoiceService(String result) {
        mListGroupView.startVoiceService(mUserId, result);
    }

    @Override
    public void loadUser(String userId) {
        mUsersRepository.getUser(userId, new UsersDataSource.GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
                mListGroupView.loadUser(user);
            }

            @Override
            public void onDataNotAvailable(String message) {

            }
        });
    }

    @Override
    public void updateList(List list) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            list.setGmtModified(simpleDateFormat.format(new Date()));
            mListsRepository.updateList(list);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteList(String listId) {
        try{
            mListsRepository.deleteList(listId, new ListsDataSource.SendRequestCallBack() {
                @Override
                public void onRequestSuccess(String message) {

                }

                @Override
                public void onRequestFail(String message) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStuffByListId(String listId) {
        try{
            mStuffsRepository.deleteStuffsByListId(listId, new StuffsDataSource.SendRequestCallBack() {
                @Override
                public void onRequestSuccess(String message) {

                }

                @Override
                public void onRequestFail(String message) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateListGroup(ListGroup listGroup) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            listGroup.setGmtModified(simpleDateFormat.format(new Date()));
            mListGroupsRepository.updateListGroup(listGroup);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void deleteListGroup(String listGroupId) {
        try{
            mListGroupsRepository.deleteListGroup(listGroupId, new ListGroupsDataSource.SendRequestCallBack() {
                @Override
                public void onRequestSuccess(String message) {

                }

                @Override
                public void onRequestFail(String message) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
