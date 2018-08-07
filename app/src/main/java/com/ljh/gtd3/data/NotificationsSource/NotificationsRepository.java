package com.ljh.gtd3.data.NotificationsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Notification;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/9.
 */

public class NotificationsRepository implements NotificationsDataSource{
    public static final String TAG = NotificationsRepository.class.getSimpleName();

    private static NotificationsRepository INSTANCE = null;

    private final NotificationsDataSource mNotificationsLocalDataSource;

    private final NotificationsDataSource mNotificationsRemoteDataSource;

    private boolean isNetWorkConnect = false;
//    Map<String,Notification> mCachedNotification;
//    //标记缓存是否有效，无效则强制更新数据
//    boolean mCacheIsDirty = false;

    private NotificationsRepository(NotificationsDataSource mNotificationsLocalDataSource, NotificationsDataSource mNotificationsRemoteDataSource) {
        this.mNotificationsLocalDataSource = mNotificationsLocalDataSource;
        this.mNotificationsRemoteDataSource = mNotificationsRemoteDataSource;
    }

    public static NotificationsRepository getInstance(@NonNull NotificationsDataSource notificationsLocalDataSource, @NonNull NotificationsDataSource notificationsRemoteDataSource){
        if(INSTANCE == null) {
            INSTANCE = new NotificationsRepository(notificationsLocalDataSource, notificationsRemoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    //在缓存、 本地、服务器获取
    @Override
    public void getNotifications(@NonNull final String userId, @NonNull final GetNotificationsCallBack callBack) {
        mNotificationsLocalDataSource.getNotifications(userId, new GetNotificationsCallBack() {
            @Override
            public void onNotificationsLoaded(List<Notification> notifications, String message) {
                callBack.onNotificationsLoaded(notifications, message);
            }

            @Override
            public void onNotificationFail(String message) {
                if(isNetWorkConnect) {
                    mNotificationsRemoteDataSource.getNotifications(userId, new GetNotificationsCallBack() {
                        @Override
                        public void onNotificationsLoaded(List<Notification> notifications, String message) {
                            callBack.onNotificationsLoaded(notifications, message);
                        }

                        @Override
                        public void onNotificationFail(String message) {
                            callBack.onNotificationFail(message);
                        }
                    });
                }
            }
        });
    }

    private void getNotificationsFromRemoteDataSource(final String userId, final GetNotificationsCallBack callBack) {
        mNotificationsRemoteDataSource.getNotifications(userId, new GetNotificationsCallBack() {
            @Override
            public void onNotificationsLoaded(List<Notification> notifications, String message) {
                refreshLocalDataSource(userId, notifications);
                callBack.onNotificationsLoaded(notifications, "success");
            }


            @Override
            public void onNotificationFail(String message) {
                callBack.onNotificationFail(message);
            }
        });
    }

    private void refreshLocalDataSource(String userId, final List<Notification> notifications) {
        mNotificationsLocalDataSource.deleteNotifications(userId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {
                for (Notification notification : notifications){
                    mNotificationsLocalDataSource.addNotification(notification);
                }
            }

            @Override
            public void onRequestFail(String message) {

            }
        });
    }

    //在缓存、本地数据库、服务器中删除
    @Override
    public void deleteNotification(@NonNull final Integer notificationId, @NonNull final SendRequestCallBack callBack) {
        //先到本地数据库删除，再到服务器中删除
        mNotificationsLocalDataSource.deleteNotification(notificationId, new SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {

            }

            @Override
            public void onRequestFail(String message) {
                callBack.onRequestFail(message);
            }
        });
        if(isNetWorkConnect) {
            mNotificationsRemoteDataSource.deleteNotification(notificationId, new SendRequestCallBack() {
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

    //从缓存、数据库中删除
    @Override
    public void deleteNotifications(@NonNull String userId, @NonNull final SendRequestCallBack callBack) {
        mNotificationsLocalDataSource.deleteNotifications(userId, new SendRequestCallBack() {
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

    //在缓存、本地、服务器更新
    @Override
    public void updateNotification(@NonNull Notification notification) {
        mNotificationsLocalDataSource.updateNotification(notification);
        mNotificationsRemoteDataSource.updateNotification(notification);

    }

    @Override
    public void addNotification(@NonNull Notification notification) {

    }

//    public void refreshNotifications(){
//        mCacheIsDirty = true;
//    }
}
