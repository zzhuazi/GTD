package com.ljh.gtd3.data.UsersSource.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljh.gtd3.data.Result;
import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.entity.Notification;
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.HttpUtil;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/8.
 */

public class UsersRemoteDataSource implements UsersDataSource {

    private static final String TAG = UsersRemoteDataSource.class.getSimpleName();
    private static UsersRemoteDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    public static UsersRemoteDataSource getInstance(@NonNull AppExecutors appExecutors){
        if(INSTANCE == null) {
            INSTANCE = new UsersRemoteDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public UsersRemoteDataSource( @NonNull AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
    }


    @Override
    public void getUser(@NonNull final String userId, @NonNull final GetUserCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/user";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userId", userId);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 网络错误，无法获得用户信息");
                        callBack.onDataNotAvailable("网络错误");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Result<User> result = UserUtility.handleUserResponse(response.body().string());
                        if(result.code == 200) {
                            callBack.onUserLoaded(result.data);
                        }else{
                            callBack.onDataNotAvailable(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void register(@NonNull final User user, @NonNull final String code, @NonNull final SendRequestCallBack callBack) {
        Log.d(TAG, "register: user: " + user.toString());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/register";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String userJson = gson.toJson(user);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userJson", userJson);
                hashMap.put("code", code);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onRequestFail("网络错误");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "注册onResponse: " + response);
                        Result<Notification> result = UserUtility.handleRegisterResponse(response.body().string());
                        if(result.code == 200) {
                            Notification notification = result.data;
                            notification.save();
                            callBack.onRequestSuccess("注册成功");
                        }else if(result.code == 100) {
                            callBack.onRequestFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }


    @Override
    public void updateUser(@NonNull final User user) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/updateUser";
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create();
                String userJson = gson.toJson(user);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userJson", userJson);
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 网络错误，无法更新用户信息");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        int i = UserUtility.handleUpdateResponse(response.body().string());
                        if(i == 1) {
                            Log.d(TAG, "onResponse: 网络请求用户更新成功");
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);

    }


    @Override
    public void deleteUser(@NonNull String userId) {

    }

    @Override
    public void login(@NonNull final String email, @NonNull final String password, @NonNull final GetUserCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/login";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("email", email);
                hashMap.put("password", password);  //无加密
                HttpUtil.sendOkHttpPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 网络错误");
                        callBack.onDataNotAvailable("网络错误，请检查网络。");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i(TAG, "onResponse: " + response);
                        Result<User> result = UserUtility.handleUserResponse(response.body().string());
                        if(result.code == 200) {
                            callBack.onUserLoaded(result.data);
                        }else{
                            callBack.onDataNotAvailable(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }

    @Override
    public void sendCode(@NonNull final String email, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String address = "http://192.168.253.1:8080/registerEmail";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sendTO", email);
                HttpUtil.sendOkHttpEmailPostRequest(address, hashMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: sendcode fail" );
                        callBack.onRequestFail("网络错误");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "发送验证码onResponse:header " + response.toString());
                        Result<User> result = UserUtility.handleCodeResponse(response.body().string());
                        if(result.code == 200) {//发送验证码成功
                            callBack.onRequestSuccess(result.msg);
                        }else {
                            callBack.onRequestFail(result.msg);
                        }
                    }
                });
            }
        };
        mAppExecutors.networkIO().execute(runnable);
    }
}
