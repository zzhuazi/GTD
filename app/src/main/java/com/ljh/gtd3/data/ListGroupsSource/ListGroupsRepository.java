package com.ljh.gtd3.data.ListGroupsSource;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.ljh.gtd3.data.ListGroupsSource.remote.ListGroupsRemoteDataSource;
import com.ljh.gtd3.data.entity.ListGroup;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MyApplication;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class ListGroupsRepository implements ListGroupsDataSource{
    public static final String TAG = ListGroupsRepository.class.getSimpleName();

    private static ListGroupsRepository INSTANCE = null;

    private final ListGroupsDataSource mListGroupsLocalDataSource;

    private final ListGroupsDataSource mListGroupsRemoteDataSource;

    private boolean isNetWorkConnect = false;

    private ListGroupsRepository(ListGroupsDataSource mListGroupsLocalDataSource, ListGroupsDataSource mListGroupsRemoteDataSource) {
        this.mListGroupsLocalDataSource = mListGroupsLocalDataSource;
        this.mListGroupsRemoteDataSource = mListGroupsRemoteDataSource;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        isNetWorkConnect = sharedPreferences.getBoolean("NETWORK", false);
    }

    public static ListGroupsRepository getInstance(@NonNull ListGroupsDataSource listGroupsLocalDataSource, @NonNull ListGroupsDataSource listGroupsRemoteDataSource){
        if(INSTANCE == null) {
            INSTANCE = new ListGroupsRepository(listGroupsLocalDataSource, listGroupsRemoteDataSource);
        }
        return  INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    //在本地获取
    @Override
    public void getListGroup(@NonNull String listGroupId, @NonNull final GetListGroupCallBack callBack) {
        mListGroupsLocalDataSource.getListGroup(listGroupId, new GetListGroupCallBack() {
            @Override
            public void onListGroupLoaded(ListGroup listGroup, String message) {
                callBack.onListGroupLoaded(listGroup, message);
            }

            @Override
            public void onListGroupFail(String message) {
                callBack.onListGroupFail(message);
            }
        });
    }

    @Override
    public void getListGroups(@NonNull final String userId, @NonNull final GetListGroupsCallBack callBack) {
        mListGroupsLocalDataSource.getListGroups(userId, new GetListGroupsCallBack() {
            @Override
            public void onListGroupsLoaded(List<ListGroup> listGroups, String message) {
                callBack.onListGroupsLoaded(listGroups, message);
            }

            @Override
            public void onListGroupsFail(String message) {
                if(isNetWorkConnect) {
                    mListGroupsRemoteDataSource.getListGroups(userId, new GetListGroupsCallBack() {
                        @Override
                        public void onListGroupsLoaded(List<ListGroup> listGroups, String message) {
                            callBack.onListGroupsLoaded(listGroups, message);
                        }

                        @Override
                        public void onListGroupsFail(String message) {
                            callBack.onListGroupsFail(message);
                        }
                    });
                }else {
                    callBack.onListGroupsFail(message);
                }
            }
        });
    }

    /**
     * 到服务器请求listgroup数据
     * @param userId
     * @param callBack
     */
    public void getListGroupsFromRemoteDateSource(@NonNull final String userId, @NonNull final GetListGroupsCallBack callBack){
        mListGroupsRemoteDataSource.getListGroups(userId, new GetListGroupsCallBack() {
            @Override
            public void onListGroupsLoaded(List<ListGroup> listGroups, String message) {
                refreshLocalDateSource(userId, listGroups);
                callBack.onListGroupsLoaded(listGroups, message);
            }

            @Override
            public void onListGroupsFail(String message) {
                getListGroups(userId, new GetListGroupsCallBack() {
                    @Override
                    public void onListGroupsLoaded(List<ListGroup> listGroups, String message) {
                        callBack.onListGroupsLoaded(listGroups, message);
                    }

                    @Override
                    public void onListGroupsFail(String message) {
                        callBack.onListGroupsFail(message);
                    }
                });
            }
        });
    }

    private void refreshLocalDateSource(String userId, List<ListGroup> listGroups) {
        for (final ListGroup listGroup : listGroups){
            mListGroupsLocalDataSource.getListGroup(listGroup.getListGroupId(), new GetListGroupCallBack() {
                @Override
                public void onListGroupLoaded(ListGroup listGroup, String message) {
                    mListGroupsLocalDataSource.updateListGroup(listGroup);
                }

                @Override
                public void onListGroupFail(String message) {
                    mListGroupsLocalDataSource.addListGroup(listGroup);
                }
            });
        }
    }

    @Override
    public void addListGroup(@NonNull ListGroup listGroup) {
        mListGroupsLocalDataSource.addListGroup(listGroup);
        if(isNetWorkConnect) {
            mListGroupsRemoteDataSource.addListGroup(listGroup);
        }
    }

    @Override
    public void updateListGroup(@NonNull ListGroup listGroup) {
        mListGroupsLocalDataSource.updateListGroup(listGroup);
        if(isNetWorkConnect) {
            mListGroupsRemoteDataSource.updateListGroup(listGroup);
        }
    }

    @Override
    public void deleteListGroup(@NonNull final String listGroupId, @NonNull final SendRequestCallBack callBack) {
        mListGroupsLocalDataSource.deleteListGroup(listGroupId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {
                if(isNetWorkConnect) {
                    mListGroupsRemoteDataSource.deleteListGroup(listGroupId, new SendRequestCallBack() {
                        @Override
                        public void onRequestSuccess(String message) {
                            callBack.onRequestSuccess("删除成功");
                        }

                        @Override
                        public void onRequestFail(String message) {
                            callBack.onRequestFail(message);
                        }
                    });
                }
            }

            @Override
            public void onRequestFail(String message) {
                callBack.onRequestFail(message);
            }
        });
    }
}
