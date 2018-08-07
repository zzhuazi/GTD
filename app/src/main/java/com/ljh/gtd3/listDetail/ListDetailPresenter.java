package com.ljh.gtd3.listDetail;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.StuffsSource.StuffsDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/29.
 */

public class ListDetailPresenter implements ListDetailContract.Presenter{
    public static final String TAG = ListDetailPresenter.class.getSimpleName();

    private UsersRepository mUsersRepository;
    private final ListsRepository mListsRepository;
    private final StuffsRepository mStuffsRepository;
    private final ListDetailContract.View mListDetailView;
    private String mUserId;
    private String mListId;

    public ListDetailPresenter(UsersRepository mUsersRepository, ListsRepository mListsRepository, StuffsRepository mStuffsRepository, ListDetailContract.View mListDetailView, String mUserId, String mListId) {
        this.mUsersRepository = mUsersRepository;
        this.mListsRepository = mListsRepository;
        this.mStuffsRepository = mStuffsRepository;
        this.mListDetailView = mListDetailView;
        this.mUserId = mUserId;
        this.mListId = mListId;
        mListDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStuffs(false);
        loadUser(mUserId);
    }

    @Override
    public void showAddStuff() {
        Map<String, String> map = new HashMap<>();
        mListDetailView.showAddStuff(map);
    }

    @Override
    public void loadStuffs(boolean forceUpdate) {
        mListDetailView.setLoadingIndicator(true);
        mListsRepository.GetList(mListId, new ListsDataSource.GetListCallBack() {
            @Override
            public void onListLoaded(final List list, String message) {
                mStuffsRepository.getStuffsByListId(mUserId, mListId, new StuffsDataSource.GetStuffsCallBack() {
                    @Override
                    public void onStuffsLoaded(java.util.List<Stuff> stuffs, String message) {
                        if(!mListDetailView.isActive()) {
                            return;
                        }
                        mListDetailView.setLoadingIndicator(false);
                        mListDetailView.showAllStuffs(list, stuffs);
                    }

                    @Override
                    public void onStuffsFail(String message) {
                        mListDetailView.showAllStuffs(list, null);
                        mListDetailView.setLoadingIndicator(false);
//                        mListDetailView.setLoadingStuffsError();
                        mListDetailView.showNoStuffs();
                    }
                });
            }

            @Override
            public void onListFail(String message) {
                mListDetailView.setLoadingIndicator(false);
                mListDetailView.setLoadingStuffsError();
                mListDetailView.showNoStuffs();
            }
        });
    }


    @Override
    public void completeStuff(@NonNull Stuff completedStuff) {
        Log.d(TAG, "completeStuff: " + completedStuff.getFinished());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        completedStuff.setFinished(true);
        completedStuff.setGmtModified(simpleDateFormat.format(new Date()));
        try{
            mStuffsRepository.updateStuff(completedStuff);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void activateStuff(@NonNull Stuff activeStuff) {
        Log.d(TAG, "activateStuff: " + activeStuff.getFinished());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        activeStuff.setFinished(false);
        activeStuff.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mStuffsRepository.updateStuff(activeStuff);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showStuffDetail(@NonNull Stuff requestStuff) {
        mListDetailView.showStuffDetail(requestStuff.getStuffId());
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
    public void deleteStuff(final Stuff stuff) {
        Log.d(TAG, "deleteStuff: ");
        try {
            mStuffsRepository.deleteStuff(stuff.getStuffId(), new StuffsDataSource.SendRequestCallBack() {
                @Override
                public void onRequestSuccess(String message) {
                    Log.d(TAG, "onRequestSuccess: " + message);
//                    mListsRepository.subtractStuffsNum(stuff.getListId());
                }

                @Override
                public void onRequestFail(String message) {
                    Log.d(TAG, "onRequestFail: " + message);
                    mListDetailView.showToast(message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showUserSetting() {
        mListDetailView.showUserSetting();
    }

    @Override
    public void startVoiceService(String result) {
        mListDetailView.startVoiceService(mUserId, result);
    }

    @Override
    public void loadUser(String userId) {
        mUsersRepository.getUser(userId, new UsersDataSource.GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
                mListDetailView.loadUser(user);
            }

            @Override
            public void onDataNotAvailable(String message) {

            }
        });
    }
}
