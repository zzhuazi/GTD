package com.ljh.gtd3.calendar;

import android.support.annotation.NonNull;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public interface CalendarContract {

    interface View extends BaseView<Presenter>{
        void setLoadingStuffsError();  //加载错误
        void showAllStuffs(List<Stuff> stuffs);  //显示所有stuffs
        void showAddStuff(Map<String, String > map);   //显示加载stuff
        void showStuffDetail(String stuffId);    //显示stuffDetial
        void showNoStuffs();  //显示没有stuffs
        boolean isActive();  //是否加载Fragment
        void showToast(String message);  //显示toast
        void startVoiceService(String userId, String result);
        void showUserSetting();
        void loadUser(User user);
        void addDecorator(Collection<CalendarDay> calendarDays);
    }

    interface Presenter extends BasePresenter{
        void showAddStuff();
        void loadStuffs(String startTime);
        void completeStuff(@NonNull Stuff completedStuff);
        void activateStuff(@NonNull Stuff activeStuff);
        void showStuffDetail(@NonNull Stuff requestStuff);
        void addStuff(Stuff stuff); //添加Stuff(处理Affair中的情况)
        void deleteStuff(Stuff stuff);
        void startVoiceService(String result);
        void showUserSetting();
        void loadUser(String userId);
        void addDecorator();
    }
}
