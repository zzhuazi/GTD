package com.ljh.gtd3.data.NotificationsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Notification;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class NotificationsLocalDataSource implements NotificationsDataSource{
    private static NotificationsLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    private NotificationsLocalDataSource(@NonNull AppExecutors appExecutors){
        mAppExecutors = appExecutors;
    }

    public static NotificationsLocalDataSource getInstance(@NonNull AppExecutors appExecutors){
        if(INSTANCE == null) {
            synchronized (NotificationsLocalDataSource.class){
                if(INSTANCE == null) {
                    INSTANCE = new NotificationsLocalDataSource(appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    @Override
    public void getNotifications(@NonNull final String userId, @NonNull final GetNotificationsCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Notification> notifications = DataSupport.where("userId = ?", userId).find(Notification.class);
                if(notifications.isEmpty()) {
                    callBack.onNotificationFail("当前用户没有通知");
                }else {
                    callBack.onNotificationsLoaded(notifications, "success");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteNotification(@NonNull final Integer notificationId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(Notification.class, "id = ?", notificationId.toString());
                if(i == 0) {
                    callBack.onRequestFail("删除失败");
                }else {
                    callBack.onRequestSuccess("删除成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteNotifications(@NonNull final String userId, @NonNull final SendRequestCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int i = DataSupport.deleteAll(Notification.class, "userId = ?", userId);
                if(i == 0) {
                    callBack.onRequestFail("删除失败");
                }else {
                    callBack.onRequestSuccess("删除成功");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateNotification(@NonNull final Notification notification) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                 notification.update(notification.getId());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addNotification(@NonNull final Notification notification) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                notification.save();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
