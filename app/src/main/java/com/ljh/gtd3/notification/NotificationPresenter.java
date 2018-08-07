package com.ljh.gtd3.notification;

import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.NotificationsSource.NotificationsDataSource;
import com.ljh.gtd3.data.NotificationsSource.NotificationsRepository;
import com.ljh.gtd3.data.UsersSource.UsersDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.entity.Notification;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/25.
 */

public class NotificationPresenter implements NotificationContact.Presenter {
    public static final String TAG = NotificationPresenter.class.getSimpleName();
    private final UsersRepository mUsersRepository;
    private final NotificationsRepository mNotificationsRepository;
    private String mUserId;
    private NotificationContact.View mNotificationView;


    public NotificationPresenter(UsersRepository mUsersRepository, NotificationsRepository mNotificationsRepository, String mUserId, NotificationContact.View mNotificationView) {
        this.mUsersRepository = mUsersRepository;
        this.mNotificationsRepository = mNotificationsRepository;
        this.mUserId = mUserId;
        this.mNotificationView = mNotificationView;
        this.mNotificationView.setPresenter(this);
    }

    @Override
    public void start() {
        loadNotifications(false, true);
        loadUser(mUserId);
    }

    @Override
    public void loadNotifications(boolean forceUpdate, boolean showLoadingUI) {
        if(showLoadingUI) {
            mNotificationView.setLoadingIndicator(true);
        }
        mNotificationsRepository.getNotifications(mUserId, new NotificationsDataSource.GetNotificationsCallBack() {
            @Override
            public void onNotificationsLoaded(List<Notification> notifications, String message) {
                if(!notifications.isEmpty()){
                    if(!mNotificationView.isActive()) {
                        return;
                    }
                    mNotificationView.setLoadingIndicator(false);
                    mNotificationView.showAllNotification(notifications);
                }else {
                    mNotificationView.setLoadingIndicator(false);
                    mNotificationView.setLoadingNotificationError();
                    mNotificationView.showNoNotification();
                }
            }

            @Override
            public void onNotificationFail(String message) {
                mNotificationView.setLoadingIndicator(false);
                mNotificationView.setLoadingNotificationError();
                mNotificationView.showNoNotification();
            }
        });
    }

    @Override
    public void deleteNotification(Integer notificationId) {
        mNotificationsRepository.deleteNotification(notificationId, new NotificationsDataSource.SendRequestCallBack() {
            @Override
            public void onRequestSuccess(String message) {

            }

            @Override
            public void onRequestFail(String message) {
                mNotificationView.showToast(message);
            }
        });
    }

    @Override
    public void readNotification(Integer notificationId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setRead(true);
        notification.setGmtModified(simpleDateFormat.format(new Date()));
        mNotificationsRepository.updateNotification(notification);
    }

    @Override
    public void loadUser(String userId) {
        mUsersRepository.getUser(userId, new UsersDataSource.GetUserCallBack() {
            @Override
            public void onUserLoaded(User user) {
                mNotificationView.loadUser(user);
            }

            @Override
            public void onDataNotAvailable(String message) {

            }
        });
    }

    @Override
    public void showUserSetting() {
        mNotificationView.showUserSetting();
    }

}
