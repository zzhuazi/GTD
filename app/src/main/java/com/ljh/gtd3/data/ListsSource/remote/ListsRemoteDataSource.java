package com.ljh.gtd3.data.ListsSource.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.Result;
import com.ljh.gtd3.data.StuffsSource.remote.StuffUtility;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.HttpUtil;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/9.
 */

public class ListsRemoteDataSource implements ListsDataSource{

    private static final String TAG = ListsRemoteDataSource.class.getSimpleName();
    private static ListsRemoteDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    private ListsRemoteDataSource(AppExecutors appExecutors){
        mAppExecutors = appExecutors;
    }

    public static ListsRemoteDataSource getInstance(@NonNull AppExecutors appExecutors){
        if(INSTANCE == null) {
            INSTANCE = new ListsRemoteDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void GetList(@NonNull final String listId, @NonNull final GetListCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/getLists";
                List list = new List();
                list.setListId(listId);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String listJson = gson.toJson(list);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listJson", listJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onListFail("服务器开小差啦..");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<java.util.List<List>> result = ListUtility.handleStuffsResponse(response.body().string());
                        if (result.code == 200) {
                            if(result.data.size() == 0) {
                                callBack.onListFail(result.msg);
                            }else {
                                callBack.onListLoaded(result.data.get(0), result.msg);
                            }
                        } else if (result.code == 100) {
                            callBack.onListFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void GetLists(@NonNull final String userId, @NonNull final GetListsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/getLists";
                List list = new List();
                list.setUserId(userId);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String listJson = gson.toJson(list);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listJson", listJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onListsFail("服务器开小差");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<java.util.List<List>> result = ListUtility.handleStuffsResponse(response.body().string());
                        if(result.code == 200) {
                            callBack.onListsLoaded(result.data, "获取清单成功");
                        }else if(result.code == 100) {
                            callBack.onListsFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void GetListsByListGroupId(@NonNull String userId, @NonNull String listGroupId, @NonNull GetListsCallBack callBack) {
        //通过ListGroupId获取多个清单只能在本地数据库中处理
    }

    @Override
    public void deleteList(@NonNull final String listId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/deleteList";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listId", listId);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onRequestFail("请检查网络");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<List> result = ListUtility.handleResultResponse(response.body().string());
                        if(result.code == 200) {
                            callBack.onRequestSuccess(result.msg);
                        }else if (result.code == 100) {
                            callBack.onRequestFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void deleteLists(@NonNull String userId, @NonNull SendRequestCallBack callBack) {
        //删除多个清单只能在本地数据库中处理
    }

    @Override
    public void updateList(@NonNull final List list) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/updateList";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String listJson = gson.toJson(list);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listJson", listJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 网络错误，更新失败" );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<Stuff> result = StuffUtility.handleResultResponse(response.body().string());
                        if(result.code == 200) {
                            Log.d(TAG, "onResponse: " + result.msg);
                        }else {
                            Log.d(TAG, "onResponse: " + result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void addList(@NonNull final List list) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/addList";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String listJson = gson.toJson(list);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("listJson", listJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 网络错误，添加失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<Stuff> result = StuffUtility.handleResultResponse(response.body().string());
                        if(result.code == 200) {
                            Log.d(TAG, "onResponse: " + result.msg);
                        }else {
                            Log.d(TAG, "onResponse: " + result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }
}
