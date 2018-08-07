package com.ljh.gtd3.data.UsersSource;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsLocalDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.ListsSource.remote.ListsRemoteDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsLocalDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.StuffsSource.remote.StuffsRemoteDataSource;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;
import com.ljh.gtd3.util.AppExecutors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by Administrator on 2018/3/8.
 */

public class UsersRepository implements UsersDataSource{
    private static final String TAG = UsersRepository.class.getSimpleName();
    private static UsersRepository INSTANCE = null;

    private final UsersDataSource mUsersLocalDataSource;

    private final UsersDataSource mUsersRemoteDataSource;

    private AppExecutors mAppExecutors;
//    Map<String, User> mCachedUser;
//
//    //标记缓存是否有效，强制更新数据请求
//    boolean mCacheIsDirty = false;

    private UsersRepository(@NonNull UsersDataSource usersLocalDataSource, @NonNull UsersDataSource usersRemoteDataSource, @NonNull AppExecutors appExecutors){
        mUsersLocalDataSource = usersLocalDataSource;
        mUsersRemoteDataSource = usersRemoteDataSource;
        mAppExecutors = appExecutors;
//        mCachedUser = new LinkedHashMap<>();
    }

    public static UsersRepository getInstance(UsersDataSource userslocalDataSource, UsersDataSource usersRemoteDataSource, AppExecutors appExecutors){
        if(INSTANCE == null ) {
            INSTANCE = new UsersRepository(userslocalDataSource, usersRemoteDataSource, appExecutors);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    //从缓存、数据库、服务器中请求数据
    @Override
    public void getUser(@NonNull final String userId, @NonNull final GetUserCallBack callBack) {

//        //如果缓存是有效的，则使用缓存
//        User cachedUser = getUserWithId(userId);
//        if(cachedUser != null) {
//            callBack.onUserLoaded(cachedUser);
//            return;
//        }

        //从数据库中获取数据，如果获取失败则到网络中获取数据
        mUsersLocalDataSource.getUser(userId, new GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
//                if(mCachedUser == null) {
//                    mCachedUser = new LinkedHashMap<>();
//                }
//                mCachedUser.put(user.getUserId(), user);
                callBack.onUserLoaded(user);
            }

            @Override
            public void onDataNotAvailable(String message) {
                mUsersRemoteDataSource.getUser(userId, new GetUserCallBack() {
                    @Override
                    public void onUserLoaded(User user) {
//                        if(mCachedUser == null) {
//                            mCachedUser = new LinkedHashMap<>();
//                        }
//                        mCachedUser.put(user.getUserId(), user);
                        callBack.onUserLoaded(user);
                    }

                    @Override
                    public void onDataNotAvailable(String message) {
                        callBack.onDataNotAvailable(message);
                    }
                });
            }
        });
    }

    @Override
    public void register(@NonNull final User user, @NonNull final String code, @NonNull final SendRequestCallBack callBack) {
        mUsersRemoteDataSource.register(user, code, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {
                //注册成功，调用本地数据源，创建user
                mUsersLocalDataSource.register(user, code, new SendRequestCallBack() {
                    @Override
                    public void onRequestSuccess(String message) {
                        //不对本地注册的回调进行处理
                    }

                    @Override
                    public void onRequestFail(String message) {
                        //不对本地注册的回调进行处理
                    }
                });
                //调用list数据仓库，添加默认的“收集箱”清单
//                final String listId = CreateDefaultList(user);
//                SystemClock.sleep(100);
//                createDefaultStuff(listId, user);
                callBack.onRequestSuccess(message);
            }

            @Override
            public void onRequestFail(String message) {
                callBack.onRequestFail(message);
            }
        });
    }

    //创建默认的材料
    private void createDefaultStuff(String listId, @NonNull User user) {
        StuffsRepository stuffsRepository = StuffsRepository.getInstance(StuffsLocalDataSource.getInstance(mAppExecutors), StuffsRemoteDataSource.getInstance(mAppExecutors));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Stuff stuff = new Stuff();
        stuff.setStuffId(UUID.randomUUID().toString());
        stuff.setName("欢迎使用GTD软件");
        stuff.setFinished(false);
        stuff.setPriority(0);
        stuff.setUserId(user.getUserId());
        stuff.setListId(listId);
        stuff.setGmtCreate(simpleDateFormat.format(new Date()));
        stuff.setGmtModified(simpleDateFormat.format(new Date()));
        stuffsRepository.addStuff(stuff);

    }

    //创建默认的清单
    private String CreateDefaultList(@NonNull User user) {
        ListsRepository listsRepository = ListsRepository.getInstance(ListsLocalDataSource.getInstance(mAppExecutors), ListsRemoteDataSource.getInstance(mAppExecutors));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List list = new List();
        String listId = UUID.randomUUID().toString();
        list.setListId(listId);
        list.setName("收集箱");
        list.setUserId(user.getUserId());
        list.setPriority(0);
        list.setStuffs(1);
        list.setGmtCreate(simpleDateFormat.format(new Date()));
        list.setGmtModified(simpleDateFormat.format(new Date()));
        list.setListGroupId(null);
        listsRepository.addList(list);
        return listId;
    }


    @Override
    public void updateUser(@NonNull User user) {
        mUsersRemoteDataSource.updateUser(user);
        mUsersLocalDataSource.updateUser(user);
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        mUsersRemoteDataSource.deleteUser(checkNotNull(userId));
        mUsersLocalDataSource.deleteUser(checkNotNull(userId));
    }

    public void login(@NonNull final String email, @NonNull final String password, @NonNull final GetUserCallBack callBack){
        //先到本地中登录
        mUsersLocalDataSource.login(email, password, new GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
                //本地登录成功
                callBack.onUserLoaded(user);
            }

            @Override
            public void onDataNotAvailable(String message) {
                //本地登录不成功，到服务器登录
                mUsersRemoteDataSource.login(email, password, new GetUserCallBack() {
                    @Override
                    public void onUserLoaded(final User user) {
                        //判断该用户是否存在
                        mUsersLocalDataSource.getUser(user.getUserId(), new GetUserCallBack() {
                            @Override
                            public void onUserLoaded(User user) {
                                //有用户存在，更新用户
                                mUsersLocalDataSource.updateUser(user);
                            }

                            @Override
                            public void onDataNotAvailable(String message) {
                                //不存在该用户，则添加到数据库中
                                user.save();
                            }
                        });
                        callBack.onUserLoaded(user);
                    }

                    @Override
                    public void onDataNotAvailable(String message) {
                        Log.d(TAG, "onDataNotAvailable: remote login fail" + message);
                        callBack.onDataNotAvailable(message);
                    }
                });
            }
        });
    }

    @Override
    public void sendCode(@NonNull String email, @NonNull final SendRequestCallBack callBack) {
        mUsersRemoteDataSource.sendCode(email, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {
                callBack.onRequestSuccess(message);
            }

            @Override
            public void onRequestFail(String message) {
                callBack.onRequestFail(message);
            }
        });
    }
}
