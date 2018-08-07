package com.ljh.gtd3.data.UsersSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.User;

/**
 * Created by Administrator on 2018/3/8.
 */

public interface UsersDataSource {
    interface LoadUserCallBack {
        void onUserLoaded(User user);

        void onDataNotAvailable();
    }

    interface GetUserCallBack {
        void onUserLoaded(User user);

        void onDataNotAvailable(String message);
    }

    interface SendRequestCallBack{
        void onRequestSuccess(String message);
        void onRequestFail(String message);
    }


    void getUser(@NonNull String userId, @NonNull GetUserCallBack callBack);

    void register(@NonNull User user, @NonNull String code, @NonNull SendRequestCallBack callBack);

    void updateUser(@NonNull User user);

    void deleteUser(@NonNull String userId);

    void login(@NonNull String email, @NonNull String password, @NonNull GetUserCallBack callBack);

    void sendCode(@NonNull String email, @NonNull SendRequestCallBack callBack);
}
