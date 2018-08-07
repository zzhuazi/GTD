package com.ljh.gtd3.data.UsersSource;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.MD5Util;

import org.litepal.crud.DataSupport;

import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;


/**
 * Created by Administrator on 2018/3/8.
 */

public class UsersLocalDataSource implements UsersDataSource {

    private static final String TAG = UsersLocalDataSource.class.getSimpleName();
    private static UsersLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    //防止直接实例化
    private UsersLocalDataSource(@NonNull AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
    }


    public static UsersLocalDataSource getInstance(@NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (UsersLocalDataSource.class){
                if (INSTANCE == null) {
                    INSTANCE = new UsersLocalDataSource(appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getUser(@NonNull final String userId, @NonNull final GetUserCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<User> users = DataSupport.where("userId = ?", userId).find(User.class);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(users.isEmpty()) {
                            callBack.onDataNotAvailable("没有该用户");
                        }else {
                            callBack.onUserLoaded(users.get(0));
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void register(@NonNull final User user, @NonNull String code, @NonNull SendRequestCallBack callBack) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                user.setPassword(MD5Util.encrypt(user.getPassword()));
                user.save();
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }


    @Override
    public void updateUser(@NonNull final User user) {
        checkNotNull(user);
        Runnable updateUser = new Runnable() {
            @Override
            public void run() {
                user.updateAll( "userId = ?", user.getUserId());
                Log.d(TAG, "run: 本地数据库更新用户成功" );
            }
        };
        mAppExecutors.diskIO().execute(updateUser);
    }

    @Override
    public void deleteUser(@NonNull final String userId) {
        Runnable deleteUser = new Runnable() {
            @Override
            public void run() {
                DataSupport.deleteAll(User.class, "userId = ?", userId);
            }
        };
        mAppExecutors.diskIO().execute(deleteUser);
    }

    @Override
    public void login(@NonNull final String email, @NonNull final String password, @NonNull final GetUserCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<User> users = DataSupport.where("email = ?", email).find(User.class);
                if(users.size() == 1) {
                    if(users.get(0).getPassword().equals(password)) {
                        callBack.onUserLoaded(users.get(0));
                    }else {
                        callBack.onDataNotAvailable("用户账号密码不正确");
                    }
                }else {
                    callBack.onDataNotAvailable("不存在该用户");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void sendCode(@NonNull String email, @NonNull SendRequestCallBack callBack) {

    }
}
