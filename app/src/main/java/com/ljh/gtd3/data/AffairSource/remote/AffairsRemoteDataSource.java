package com.ljh.gtd3.data.AffairSource.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljh.gtd3.data.AffairSource.AffairsDataSource;
import com.ljh.gtd3.data.Result;
import com.ljh.gtd3.data.StuffsSource.remote.StuffUtility;
import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/9.
 */

public class AffairsRemoteDataSource implements AffairsDataSource{
    private static final String TAG = AffairsRemoteDataSource.class.getSimpleName();
    private static AffairsRemoteDataSource INSTANCE;
    private AppExecutors mAppExecutors;

    private AffairsRemoteDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static AffairsRemoteDataSource getInstance(AppExecutors appExecutors){
        if(INSTANCE == null) {
            INSTANCE = new AffairsRemoteDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getAffair(@NonNull String affairId, @NonNull GetAffairCallBack callBack) {
        //在本地数据库中查找
    }

    @Override
    public void getAffairs(@NonNull final String stuffId, @NonNull final GetAffairsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/getAffairs";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("stuffId", stuffId);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onAffairsFail("服务器开小猜！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<List<Affair>> result = AffairUtility.handleAffairsResponse(response.body().string());
                        if(result.code == 200) {
                            callBack.onAffairsLoaded(result.data, result.msg);
                        }else if(result.code == 100) {
                            callBack.onAffairsFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void addAffair(@NonNull final Affair affair) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/addAffair";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String affairJson = gson.toJson(affair);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("affairJson", affairJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "服务器开小差");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<Stuff> result = StuffUtility.handleResultResponse(response.body().string());
                        if (result != null) {
                            Log.d(TAG, "onResponse: " + result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void updateAffair(@NonNull final Affair affair) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/updateAffair";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String affairJson = gson.toJson(affair);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("affairJson", affairJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "服务器开小猜");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: remote update");
                        Result<Stuff> result = StuffUtility.handleResultResponse(response.body().string());
                        if (result != null) {
                            Log.d(TAG, "onResponse: " + result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void deleteAffair(@NonNull final String affairId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/deleteAffair";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("affairId", affairId);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "网络错误，删除失败");
                        callBack.onRequestFail("网络错误，删除失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<Stuff> result = StuffUtility.handleResultResponse(response.body().string());
                        if (result.code == 200) {
                            callBack.onRequestSuccess(result.msg);
                        } else if (result.code == 100) {
                            callBack.onRequestFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void deleteAffairs(@NonNull final String stuffId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/deleteAffairs";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("stuffId", stuffId);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "网络错误，删除失败");
                        callBack.onRequestFail("网络错误，删除失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<Stuff> result = StuffUtility.handleResultResponse(response.body().string());
                        if (result.code == 200) {
                            callBack.onRequestSuccess(result.msg);
                        } else if (result.code == 100) {
                            callBack.onRequestFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }
}
