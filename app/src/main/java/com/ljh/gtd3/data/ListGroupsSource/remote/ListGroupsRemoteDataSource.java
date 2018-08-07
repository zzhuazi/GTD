package com.ljh.gtd3.data.ListGroupsSource.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljh.gtd3.data.ListGroupsSource.ListGroupsDataSource;
import com.ljh.gtd3.data.Result;
import com.ljh.gtd3.data.entity.ListGroup;
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

public class ListGroupsRemoteDataSource implements ListGroupsDataSource {
    public static final String TAG = ListGroupsRemoteDataSource.class.getSimpleName();
    private static ListGroupsRemoteDataSource INSTANCE;
    private AppExecutors mAppExecutors;

    private ListGroupsRemoteDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static ListGroupsRemoteDataSource getInstance(AppExecutors appExecutors) {
        if (INSTANCE == null) {
            INSTANCE = new ListGroupsRemoteDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getListGroup(@NonNull String listGroupId, @NonNull GetListGroupCallBack callBack) {
        //在本地获取
    }

    @Override
    public void getListGroups(@NonNull final String userId, @NonNull final GetListGroupsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/getListGroups";
                ListGroup listGroup = new ListGroup();
                listGroup.setUserId(userId);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String listGroupJson = gson.toJson(listGroup);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listGroupJson", listGroupJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onListGroupsFail("服务器开小猜！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<List<ListGroup>> result = ListGroupUtility.handleListGroupsResponse(response.body().string());
                        if (result.code == 200) {
                            callBack.onListGroupsLoaded(result.data, result.msg);
                        } else if (result.code == 100) {
                            callBack.onListGroupsFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void addListGroup(@NonNull final ListGroup listGroup) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/addListGroup";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String listGroupJson = gson.toJson(listGroup);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listGroupJson", listGroupJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "服务器开小差");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<ListGroup> result = ListGroupUtility.handleResultResponse(response.body().string());
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
    public void updateListGroup(@NonNull final ListGroup listGroup) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/updateListGroup";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String listGroupJson = gson.toJson(listGroup);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listGroupJson", listGroupJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "服务器开小差");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<ListGroup> result = ListGroupUtility.handleResultResponse(response.body().string());
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
    public void deleteListGroup(@NonNull final String listGroupId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/deleteListGroup";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listGroupId", listGroupId);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "服务器开小差");
                        callBack.onRequestFail("网络错误，删除失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<ListGroup> result = ListGroupUtility.handleResultResponse(response.body().string());
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
