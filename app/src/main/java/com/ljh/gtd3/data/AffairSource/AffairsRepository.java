package com.ljh.gtd3.data.AffairSource;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.util.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class AffairsRepository implements AffairsDataSource {
    public static final String TAG = AffairsRepository.class.getSimpleName();
    private static AffairsRepository INSTANCE = null;

    private final AffairsDataSource mAffairsLocalDataSource;

    private final AffairsDataSource mAffairsRemoteDataSource;

    private boolean isNetWorkConnect = false;

    private AffairsRepository(AffairsDataSource mAffairsLocalDataSource, AffairsDataSource mAffairsRemoteDataSource) {
        this.mAffairsLocalDataSource = mAffairsLocalDataSource;
        this.mAffairsRemoteDataSource = mAffairsRemoteDataSource;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        isNetWorkConnect =  sharedPreferences.getBoolean("NETWORK", false);
    }

    public static AffairsRepository getInstance(@NonNull AffairsDataSource affairsLocalDataSource, AffairsDataSource affairsRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new AffairsRepository(affairsLocalDataSource, affairsRemoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    //只在本地数据库获取
    @Override
    public void getAffair(@NonNull final String affairId, @NonNull final GetAffairCallBack callBack) {
        mAffairsLocalDataSource.getAffair(affairId, new GetAffairCallBack() {
            @Override
            public void onAffairLoaded(Affair affair, String message) {
                callBack.onAffairLoaded(affair, message);
            }

            @Override
            public void onAffairFail(String message) {
                callBack.onAffairFail(message);
            }
        });
    }

    @Override
    public void getAffairs(@NonNull final String stuffId, @NonNull final GetAffairsCallBack callBack) {
        //到数据库取affairs
        mAffairsLocalDataSource.getAffairs(stuffId, new GetAffairsCallBack() {
            @Override
            public void onAffairsLoaded(List<Affair> affairs, String message) {
                callBack.onAffairsLoaded(affairs, message);
            }

            @Override
            public void onAffairsFail(String message) {
                //判断网络是否可用，数据库无数据，则到服务器取数据
                if(isNetWorkConnect) {
                    mAffairsRemoteDataSource.getAffairs(stuffId, new GetAffairsCallBack() {
                        @Override
                        public void onAffairsLoaded(List<Affair> affairs, String message) {
                            callBack.onAffairsLoaded(affairs,message);
                        }

                        @Override
                        public void onAffairsFail(String message) {
                            callBack.onAffairsFail(message);
                        }
                    });
                }
            }
        });
    }

    public void getAffairsFromRemoteDataSource(final String stuffId, final GetAffairsCallBack callBack){
        mAffairsRemoteDataSource.getAffairs(stuffId, new GetAffairsCallBack() {
            @Override
            public void onAffairsLoaded(List<Affair> affairs, String message) {
                refreshLocalDateSource(stuffId, affairs);
                callBack.onAffairsLoaded(affairs, message);
            }

            @Override
            public void onAffairsFail(String message) {
                callBack.onAffairsFail(message);
            }
        });
    }

    private void refreshLocalDateSource(String stuffId, List<Affair> affairs) {
        for (final Affair affair : affairs){
            mAffairsLocalDataSource.getAffair(affair.getAffairId(), new GetAffairCallBack() {
                @Override
                public void onAffairLoaded(Affair affair, String message) {
                    mAffairsLocalDataSource.updateAffair(affair);
                }

                @Override
                public void onAffairFail(String message) {
                    mAffairsLocalDataSource.addAffair(affair);
                }
            });
        }
    }

    @Override
    public void addAffair(@NonNull Affair affair) {
        mAffairsLocalDataSource.addAffair(affair);
        if(isNetWorkConnect) {
            mAffairsRemoteDataSource.addAffair(affair);
        }
    }

    @Override
    public void updateAffair(@NonNull Affair affair) {
        mAffairsLocalDataSource.updateAffair(affair);
        if(isNetWorkConnect) {
            mAffairsRemoteDataSource.updateAffair(affair);
        }
    }

    @Override
    public void deleteAffair(@NonNull final String affairId, @NonNull final SendRequestCallBack callBack) {
        mAffairsLocalDataSource.deleteAffair(affairId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {

            }

            @Override
            public void onRequestFail(String message) {
                callBack.onRequestFail(message);
            }
        });
        //如果能连接服务器，就到服务器中删除
        if(isNetWorkConnect) {
            mAffairsRemoteDataSource.deleteAffair(affairId, new SendRequestCallBack() {
                @Override
                public void onRequestSuccess(String message) {

                }

                @Override
                public void onRequestFail(String message) {
                    callBack.onRequestFail(message);
                }
            });
        }
        callBack.onRequestSuccess("删除成功");
    }

    @Override
    public void deleteAffairs(@NonNull String stuffId, @NonNull final SendRequestCallBack callBack) {
        mAffairsLocalDataSource.deleteAffairs(stuffId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {

            }

            @Override
            public void onRequestFail(String message) {
                callBack.onRequestFail(message);
            }
        });
        //如果能连接服务器，就到服务器中删除
        if(isNetWorkConnect) {
            mAffairsRemoteDataSource.deleteAffairs(stuffId, new SendRequestCallBack() {
                @Override
                public void onRequestSuccess(String message) {

                }

                @Override
                public void onRequestFail(String message) {
                    callBack.onRequestFail(message);
                }
            });
        }
        callBack.onRequestSuccess("删除成功");
    }
}
