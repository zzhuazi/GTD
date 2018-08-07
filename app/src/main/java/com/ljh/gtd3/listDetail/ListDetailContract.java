package com.ljh.gtd3.listDetail;

import android.support.annotation.NonNull;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;

import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public interface ListDetailContract {
    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);   //正在加载指示
        void setLoadingStuffsError();  //加载错误
        void showAllStuffs(List list, java.util.List<Stuff> stuffs);  //显示所有stuffs
        void showAddStuff(Map<String, String > map);   //显示加载stuff
        void showStuffDetail(String stuffId);    //显示stuffDetial
        void showNoStuffs();  //显示没有stuffs
        boolean isActive();  //是否加载Fragment
        void showToast(String message);  //显示toast
        void showUserSetting();
        void startVoiceService(String userId, String result);
        void loadUser(User user);
    }

    interface Presenter extends BasePresenter{
        void showAddStuff();
        void loadStuffs(boolean forceUpdate);
        void completeStuff(@NonNull Stuff completedStuff);
        void activateStuff(@NonNull Stuff activeStuff);
        void showStuffDetail(@NonNull Stuff requestStuff);
        void addStuff(Stuff stuff); //添加Stuff(处理Affair中的情况)
        void deleteStuff(Stuff stuff);
        void showUserSetting();
        void startVoiceService(String result);
        void loadUser(String userId);
    }
}
