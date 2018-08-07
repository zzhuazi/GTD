package com.ljh.gtd3.data.ListsSource;

import android.support.annotation.NonNull;
import android.util.Log;

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

    private final ListsDataSource mListsRemoteDataSource;

//    Map<String, List> mCachedList;
//
//    //标记缓存是否有效，无效则强制更新数据
//    boolean mCacheIsDirty = false;

    private ListsRepository(ListsDataSource mListsLocalDataSource, ListsDataSource mListsRemoteDataSource) {
        this.mListsLocalDataSource = mListsLocalDataSource;
        this.mListsRemoteDataSource = mListsRemoteDataSource;
//        mCachedList = new LinkedHashMap<>();
    }

    public static ListsRepository getInstance(@NonNull ListsDataSource listsLocalDataSource, @NonNull ListsDataSource listsRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new ListsRepository(listsLocalDataSource, listsRemoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    //在缓存，本地获取
    @Override
    public void GetList(@NonNull String listId, @NonNull final GetListCallBack callBack) {
//        List cachedList = getListWithId(listId);
//
//        //如果缓存有效则返回缓存数据
//        if (cachedList != null) {
//            callBack.onListLoaded(cachedList, "获取成功");
//            return;
//        }

        //否则在本地数据库中获取
        mListsLocalDataSource.GetList(listId, new GetListCallBack() {
            @Override
            public void onListLoaded(List list, String message) {
//                if (mCachedList == null) {
//                    mCachedList = new LinkedHashMap<>();
//                }
//                mCachedList.put(list.getListId(), list);
                callBack.onListLoaded(list, "获取清单成功");
            }

            @Override
            public void onListFail(String message) {
                callBack.onListFail(message);
            }
        });
    }

//    private List getListWithId(String listId) {
//        if (mCachedList == null || mCachedList.isEmpty()) {
//            return null;
//        } else {
//            return mCachedList.get(listId);
//        }
//    }

    //在缓存、本地、服务器中获取
    @Override
    public void GetLists(@NonNull final String userId, @NonNull final GetListsCallBack callBack) {
//        Log.d(TAG, "GetLists: mCachedList is null?" + (mCachedList == null));
//        Log.d(TAG, "GetLists: mCacheIsDirty is ?" + mCacheIsDirty);
//        if (mCachedList != null && !mCacheIsDirty) {
//            callBack.onListsLoaded(new ArrayList<List>(mCachedList.values()), "获取成功");
//            return;
//        }
//
//        if (mCacheIsDirty) {
//            Log.d(TAG, "GetLists: ");
//            getListsFromRemoteDataSource(userId, callBack);
//        } else {
            //到本地数据库中查询数据，如果数据无效则到服务器中查询
            mListsLocalDataSource.GetLists(userId, new GetListsCallBack() {
                @Override
                public void onListsLoaded(java.util.List<List> lists, String message) {
                    Log.d(TAG, "onListsLoaded: " + lists.size());
                    callBack.onListsLoaded(lists, "获取成功");
                }

                @Override
                public void onListsFail(String message) {
                    //到服务器中获取
//                    getListsFromRemoteDataSource(userId, callBack);
                    mListsRemoteDataSource.GetLists(userId, new GetListsCallBack() {
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
            });
    }

    public void getListsFromRemoteDataSource(final String userId, final GetListsCallBack callBack) {
        mListsRemoteDataSource.GetLists(userId, new GetListsCallBack() {
            @Override
            public void onListsLoaded(java.util.List<List> lists, String message) {
                refreshLocalDataSource(userId, lists);
                callBack.onListsLoaded(lists, "更新成功");
            }

            @Override
            public void onListsFail(String message) {
                callBack.onListsFail("缓存更新失败");
            }
        });
    }

    private void refreshLocalDataSource(String userId, final java.util.List<List> lists) {
        for (final List list : lists) {
            mListsLocalDataSource.GetList(list.getListId(), new GetListCallBack() {
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

//    private void refreshCache(java.util.List<List> lists) {
//        mCachedList = new LinkedHashMap<>();
//        for (List list : lists) {
//            mCachedList.put(list.getListId(), list);
//        }
//        mCacheIsDirty = false;
//    }

    //在缓存、本地数据库中获取
    @Override
    public void GetListsByListGroupId(@NonNull String userId, @NonNull String listGroupId, @NonNull final GetListsCallBack callBack) {
        mListsLocalDataSource.GetListsByListGroupId(userId, listGroupId, new GetListsCallBack() {
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

    //在缓存、本地数据库、服务器中删除
    @Override
    public void deleteList(@NonNull final String listId, @NonNull final SendRequestCallBack callBack) {
        //先从本地数据库删除、再到服务器中删除
        mListsLocalDataSource.deleteList(listId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {
                mListsRemoteDataSource.deleteList(listId, new SendRequestCallBack() {
                    @Override
                    public void onRequestSuccess(String message) {
                        callBack.onRequestSuccess(message);
                    }

                    @Override
                    public void onRequestFail(String message) {
                        //服务器中删除失败，要如何处理？
                        callBack.onRequestFail(message);
                    }
                });
            }

            @Override
            public void onRequestFail(String message) {
                callBack.onRequestFail(message);
            }
        });
    }

    //从缓存、本地删除
    @Override
    public void deleteLists(@NonNull String userId, @NonNull final SendRequestCallBack callBack) {

        mListsLocalDataSource.deleteLists(userId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {
                callBack.onRequestSuccess(message);
            }

            @Override
            public void onRequestFail(String message) {
                callBack.onRequestFail(message);
            }
        });
    }

    //在缓存、本地、服务器更新
    @Override
    public void updateList(@NonNull final List list) {
        mListsLocalDataSource.updateList(list);
        mListsRemoteDataSource.GetList(list.getListId(), new GetListCallBack() {
            @Override
            public void onListLoaded(List list, String message) {
                //服务器中有该数据，则提交更新
                mListsRemoteDataSource.updateList(list);
            }

            @Override
            public void onListFail(String message) {
                //服务器中无该数据，则添加该数据
                mListsRemoteDataSource.addList(list);
            }
        });
    }

    //在缓存、 本地、服务器中添加
    @Override
    public void addList(@NonNull List list) {
        mListsRemoteDataSource.addList(list);
        mListsLocalDataSource.addList(list);
//        if (mCachedList == null) {
//            mCachedList = new LinkedHashMap<>();
//        }
//        mCachedList.put(list.getListId(), list);
    }

    public void updateStuffsNum(String listId) {
        mListsLocalDataSource.GetList(listId, new GetListCallBack() {
            @Override
            public void onListLoaded(List list, String message) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                list.setStuffs(list.getStuffs() + 1);
                list.setGmtModified(simpleDateFormat.format(new Date()));
                mListsLocalDataSource.updateList(list);
                mListsRemoteDataSource.updateList(list);
            }

            @Override
            public void onListFail(String message) {

            }
        });
    }

    public void subtractStuffsNum(String listId) {
        mListsLocalDataSource.GetList(listId, new GetListCallBack() {
            @Override
            public void onListLoaded(List list, String message) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                list.setStuffs(list.getStuffs() - 1);
                list.setGmtModified(simpleDateFormat.format(new Date()));
                mListsLocalDataSource.updateList(list);
                mListsRemoteDataSource.updateList(list);
            }

            @Override
            public void onListFail(String message) {

            }
        });
    }
}
