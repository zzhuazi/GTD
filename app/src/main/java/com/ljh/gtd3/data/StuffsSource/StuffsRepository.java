package com.ljh.gtd3.data.StuffsSource;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.ListsSource.remote.ListsRemoteDataSource;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MyApplication;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class StuffsRepository implements StuffsDataSource {
    public static final String TAG = StuffsRepository.class.getSimpleName();
    private static StuffsRepository INSTANCE = null;

    private final StuffsDataSource mStuffsLocalDataSource;

    private final StuffsDataSource mStuffsRemoteDataSource;

    private boolean isNetWorkConnect = false;

    private StuffsRepository(@NonNull StuffsDataSource stuffsLocalDataSource, @NonNull StuffsDataSource stuffsRemoteDataSource) {
        mStuffsLocalDataSource = stuffsLocalDataSource;
        mStuffsRemoteDataSource = stuffsRemoteDataSource;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        isNetWorkConnect = sharedPreferences.getBoolean("NETWORK", false);
    }

    public static StuffsRepository getInstance(@NonNull StuffsDataSource stuffsLocalDataSource, @NonNull StuffsDataSource stuffsRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new StuffsRepository(stuffsLocalDataSource, stuffsRemoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    //在缓存、本地获取
    @Override
    public void getStuff(@NonNull final String stuffId, @NonNull final GetStuffCallBack callBack) {
        //在本地数据库获取
        mStuffsLocalDataSource.getStuff(stuffId, new GetStuffCallBack() {
            @Override
            public void onStuffLoaded(Stuff stuff, String message) {
                callBack.onStuffLoaded(stuff, "获取成功");
            }

            @Override
            public void onStuffFail(String message) {
                callBack.onStuffFail(message);
            }
        });
    }

    //在缓存、本地、服务器获取
    @Override
    public void getAllStuffs(@NonNull final String userId, @NonNull final GetStuffsCallBack callBack) {
        //到本地数据库中查询数据，如果数据无效则到服务器中查询
        mStuffsLocalDataSource.getAllStuffs(userId, new GetStuffsCallBack() {
            @Override
            public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                callBack.onStuffsLoaded(stuffs,message);
            }

            @Override
            public void onStuffsFail(String message) {
                if (isNetWorkConnect) {
                    mStuffsRemoteDataSource.getAllStuffs(userId, new GetStuffsCallBack() {
                        @Override
                        public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                            callBack.onStuffsLoaded(stuffs, message);
                        }

                        @Override
                        public void onStuffsFail(String message) {
                            callBack.onStuffsFail(message);
                        }
                    });
                }
            }
        });
    }

    public void getStuffsFromRemoteDateSource(final String userId, final GetStuffsCallBack callBack){
        mStuffsRemoteDataSource.getAllStuffs(userId, new GetStuffsCallBack() {
            @Override
            public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                refreshLocalDateSource(userId, stuffs);
                callBack.onStuffsLoaded(stuffs, message);
            }

            @Override
            public void onStuffsFail(String message) {
                getAllStuffs(userId, new GetStuffsCallBack() {
                    @Override
                    public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                        callBack.onStuffsLoaded(stuffs,message);
                    }

                    @Override
                    public void onStuffsFail(String message) {
                        callBack.onStuffsFail(message);
                    }
                });
            }
        });
    }

    private void refreshLocalDateSource(String userId, List<Stuff> stuffs) {
        for (final Stuff stuff: stuffs){
            mStuffsLocalDataSource.getStuff(stuff.getStuffId(), new GetStuffCallBack() {
                @Override
                public void onStuffLoaded(Stuff stuff, String message) {
                    mStuffsLocalDataSource.updateStuff(stuff);
                }

                @Override
                public void onStuffFail(String message) {
                    mStuffsLocalDataSource.addStuff(stuff);
                }
            });
        }
    }

    //在缓存、本地获取
    @Override
    public void getStuffsByListId(@NonNull String userId, @NonNull String listId, @NonNull final GetStuffsCallBack callBack) {
        //再根据listId获取stuffs
        mStuffsLocalDataSource.getStuffsByListId(userId, listId, new GetStuffsCallBack() {
            @Override
            public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                callBack.onStuffsLoaded(stuffs, message);
            }

            @Override
            public void onStuffsFail(String message) {
                callBack.onStuffsFail(message);
            }
        });
    }

    //在缓存、本地获取
    @Override
    public void getStuffsByStartDate(@NonNull String userId, @NonNull String startDate, @NonNull final GetStuffsCallBack callBack) {
        mStuffsLocalDataSource.getStuffsByStartDate(userId, startDate, new GetStuffsCallBack() {
            @Override
            public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                callBack.onStuffsLoaded(stuffs, message);
            }

            @Override
            public void onStuffsFail(String message) {
                callBack.onStuffsFail(message);
            }
        });
    }

    //从缓存、本地、服务器删除
    @Override
    public void deleteStuff(@NonNull final String stuffId, @NonNull final SendRequestCallBack callBack) {
        getStuff(stuffId, new GetStuffCallBack() {
            @Override
            public void onStuffLoaded(final Stuff stuff, String message) {
                //先从本地数据库删除，再到服务器中删除
                mStuffsLocalDataSource.deleteStuff(stuffId, new SendRequestCallBack() {
                    @Override
                    public void onRequestSuccess(String message) {
                        AppExecutors appExecutors = new AppExecutors();
                        ListsRepository listsRepository = ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors), ListsRemoteDataSource.getInstance(appExecutors));
                        listsRepository.subtractStuffsNum(stuff.getListId());
                        if (isNetWorkConnect) {
                            mStuffsRemoteDataSource.deleteStuff(stuffId, new SendRequestCallBack() {
                                @Override
                                public void onRequestSuccess(String message) {
                                    callBack.onRequestSuccess("删除成功");
                                }

                                @Override
                                public void onRequestFail(String message) {
                                    callBack.onRequestFail(message);
                                }
                            });
                        }else{
                            callBack.onRequestSuccess("删除成功");
                        }
                    }

                    @Override
                    public void onRequestFail(String message) {
                        callBack.onRequestFail(message);
                    }
                });

            }

            @Override
            public void onStuffFail(String message) {
                callBack.onRequestFail(message);
            }
        });

    }

    //从缓存、本地删除
    @Override
    public void deleteStuffs(@NonNull String userId, @NonNull final SendRequestCallBack callback) {
        mStuffsLocalDataSource.deleteStuffs(userId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {
                callback.onRequestSuccess(message);
            }

            @Override
            public void onRequestFail(String message) {
                callback.onRequestFail(message);
            }
        });
    }

    @Override
    public void deleteStuffsByListId(@NonNull final String listId, @NonNull final SendRequestCallBack callback) {
        mStuffsRemoteDataSource.deleteStuffsByListId(listId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {
                if(isNetWorkConnect) {
                    mStuffsRemoteDataSource.deleteStuffsByListId(listId, new SendRequestCallBack() {
                        @Override
                        public void onRequestSuccess(String message) {
                            callback.onRequestSuccess("删除成功");
                        }

                        @Override
                        public void onRequestFail(String message) {
                            callback.onRequestFail(message);
                        }
                    });
                }
            }

            @Override
            public void onRequestFail(String message) {
                callback.onRequestFail(message);
            }
        });
    }

    //在缓存、本地、服务器更新
    @Override
    public void updateStuff(@NonNull final Stuff stuffUpdate) {
        //本地数据库更新
        mStuffsLocalDataSource.updateStuff(stuffUpdate);
        if (isNetWorkConnect) {
            mStuffsRemoteDataSource.updateStuff(stuffUpdate);
        }
    }

    //在缓存、本地、服务器添加
    @Override
    public void addStuff(@NonNull Stuff stuff) {
        if (isNetWorkConnect) {
            mStuffsRemoteDataSource.addStuff(stuff);
        }
        mStuffsLocalDataSource.addStuff(stuff);
        AppExecutors appExecutors = new AppExecutors();
        ListsRepository listsRepository = ListsRepository.getInstance(ListsLocalDataSource.getInstance(appExecutors), ListsRemoteDataSource.getInstance(appExecutors));
        listsRepository.updateStuffsNum(stuff.getListId());
    }
}