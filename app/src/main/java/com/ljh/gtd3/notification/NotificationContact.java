package com.ljh.gtd3.notification;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.Notification;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/25.
 */

public interface NotificationContact {
    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);  //正在加载提示
        void setLoadingNotificationError();  //加载错误
        void showAllNotification(List<Notification> notifications);
        void showNoNotification();
        boolean isActive();  //是否加载fragment
        void showToast(String message);   //显示Toast提示
        void showUserSetting();
        void loadUser(User user);
    }

    interface Presenter extends BasePresenter{
        void loadNotifications(boolean forceUpdate, boolean showLoadingUI);
        void deleteNotification(Integer notificationId);
        void readNotification(Integer notificationId);
        void loadUser(String userId);
        void showUserSetting();
    }
}
