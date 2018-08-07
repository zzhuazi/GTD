package com.ljh.gtd3.data.NotificationsSource.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljh.gtd3.data.NotificationsSource.NotificationsDataSource;
import com.ljh.gtd3.data.Result;
import com.ljh.gtd3.data.entity.Notification;
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

public class NotificationsRemoteDataSource implements NotificationsDataSource{
    private static final String TAG = NotificationsRemoteDataSource.class.getSimpleName();
    private static NotificationsRemoteDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    private NotificationsRemoteDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static NotificationsRemoteDataSource getInstance(@NonNull AppExecutors appExecutors){
        if(INSTANCE == null) {
            INSTANCE = new NotificationsRemoteDataSource(appExecutors);
        }
        return  INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getNotifications(@NonNull final String userId, @NonNull final GetNotificationsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/getNotifications";
                Notification notification = new Notification();
                notification.setUserId(userId);
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String notificationJson = gson.toJson(notification);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("notificationJson", notificationJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onNotificationFail("服务器开小差..");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<List<Notification>> result = NotificationUtility.handleNotificationsResponse(response.body().string());
                        if (result.code == 200) {
                            callBack.onNotificationsLoaded(result.data, result.msg);
                        } else if (result.code == 100) {
                            callBack.onNotificationFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void deleteNotification(@NonNull final Integer notificationId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/deleteNotification";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("notificationId", notificationId);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onRequestFail("服务器开小差了...");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<Notification> result = NotificationUtility.handleResultResponse(response.body().string());
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
    public void deleteNotifications(@NonNull String userId, @NonNull SendRequestCallBack callBack) {
        //仅在本地数据库中进行删除
    }

    @Override
    public void updateNotification(@NonNull final Notification notification) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/updateNotification";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String notificationJson = gson.toJson(notification);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("notificationJson", notificationJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + "服务器开小猜");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: remote update");
                        Result<Notification> result = NotificationUtility.handleResultResponse(response.body().string());
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
    public void addNotification(@NonNull final Notification notification) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/addNotification";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String notificationJson = gson.toJson(notification);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("notificationJson", notificationJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 服务器开小差");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: remote update");
                        Result<Notification> result = NotificationUtility.handleResultResponse(response.body().string());
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
