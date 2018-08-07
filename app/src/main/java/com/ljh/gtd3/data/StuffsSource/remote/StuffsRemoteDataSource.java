package com.ljh.gtd3.data.StuffsSource.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljh.gtd3.data.Result;
import com.ljh.gtd3.data.StuffsSource.StuffsDataSource;
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

public class StuffsRemoteDataSource implements StuffsDataSource {

    private static final String TAG = StuffsRemoteDataSource.class.getSimpleName();

    private static StuffsRemoteDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    private StuffsRemoteDataSource(AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
    }

    public static StuffsRemoteDataSource getInstance(@NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            INSTANCE = new StuffsRemoteDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getStuff(@NonNull final String stuffId, @NonNull final GetStuffCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/getStuffs";
                Stuff stuff = new Stuff();
                stuff.setStuffId(stuffId);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String stuffJson = gson.toJson(stuff);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("stuffJson", stuffJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onStuffFail("服务器开小差");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<List<Stuff>> result = StuffUtility.handleStuffsResponse(response.body().string());
                        if (result.code == 200) {
                            callBack.onStuffLoaded(result.data.get(0), result.msg);
                        } else if (result.code == 100) {
                            callBack.onStuffFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void getAllStuffs(@NonNull final String userId, @NonNull final GetStuffsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/getStuffs";
                Stuff stuff = new Stuff();
                stuff.setUserId(userId);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String stuffJson = gson.toJson(stuff);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("stuffJson", stuffJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onStuffsFail("网络错误");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<List<Stuff>> result = StuffUtility.handleStuffsResponse(response.body().string());
                        if (result.code == 200) {
                            callBack.onStuffsLoaded(result.data, result.msg);
                        } else if (result.code == 100) {
                            callBack.onStuffsFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void getStuffsByListId(@NonNull String userId, @NonNull String listId, @NonNull GetStuffsCallBack callBack) {
        //根据listId获取stuffs只在本地数据库中进行
    }

    @Override
    public void getStuffsByStartDate(@NonNull String userId, @NonNull String startDate, @NonNull GetStuffsCallBack callBack) {
        //根据startDate获取stuffs只在本地数据库中进行
    }

    @Override
    public void deleteStuff(@NonNull final String stuffId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/deleteStuff";
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

    @Override
    public void deleteStuffs(@NonNull String userId, @NonNull SendRequestCallBack callBack) {
        //删除所有stuff只在本地数据库进行
    }

    @Override
    public void deleteStuffsByListId(@NonNull final String listId, @NonNull final SendRequestCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/deleteStuffByListId";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listId", listId);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "网络错误，删除失败");
                        callback.onRequestFail("网络错误，删除失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<Stuff> result = StuffUtility.handleResultResponse(response.body().string());
                        if (result.code == 200) {
                            callback.onRequestSuccess(result.msg);
                        } else if (result.code == 100) {
                            callback.onRequestFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void updateStuff(@NonNull final Stuff stuff) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/updateStuff";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String stuffJson = gson.toJson(stuff);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("stuffJson", stuffJson);
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
    public void addStuff(@NonNull final Stuff stuff) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/addStuff";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String stuffJson = gson.toJson(stuff);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("stuffJson", stuffJson);
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
}
