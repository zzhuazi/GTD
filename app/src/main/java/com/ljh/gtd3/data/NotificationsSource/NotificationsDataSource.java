package com.ljh.gtd3.data.NotificationsSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Notification;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public interface NotificationsDataSource {

    //获取Notifications回调
    interface GetNotificationsCallBack{
        void onNotificationsLoaded(List<Notification> notifications, String message);
        void onNotificationFail(String message);
    }

    //获取网络请求结果回调
    interface SendRequestCallBack{
        void onRequestSuccess(String message);
        void onRequestFail(String message);
    }

    void getNotifications(@NonNull String userId, @NonNull GetNotificationsCallBack callBack);

    void deleteNotification(@NonNull Integer notificationId, @NonNull SendRequestCallBack callBack);

    void deleteNotifications(@NonNull String userId, @NonNull SendRequestCallBack callBack);

    void updateNotification(@NonNull Notification notification);

    void addNotification(@NonNull Notification notification);
}
