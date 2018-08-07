package com.ljh.gtd3.listGroup;

import android.support.annotation.NonNull;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.ListGroup;
import com.ljh.gtd3.data.entity.Stuff;
import com.ljh.gtd3.data.entity.User;

/**
 * Created by Administrator on 2018/3/16.
 */

public interface ListGroupContract {

    interface View extends BaseView<Presenter> {
        boolean isActive();  //fragment是否获取
        void showListDetail(@NonNull List requestList); //跳转到ListDetail页面，中有stuffs
        void showListSetting(@NonNull List requestList);  //跳转到list修改页面
        void showAddStuff();  //跳转到添加stuff页面
        void setLoadingIndicator(boolean active);
        void setLoadingStuffsError();
        void showAllLists(java.util.List<ListGroup> listGroups, java.util.List<java.util.List<List>> lists);
        void showToast(String message);
        void showUserSetting();
        void startVoiceService(String userId, String result);
        void loadUser(User user);
        void showAddListGroup();  //弹出对话框，添加文件夹
    }

    interface Presenter extends BasePresenter{
        void showAddStuff();
        void addListGroup(ListGroup listGroup);
        void addStuff(Stuff stuff);  //语音功能的添加stuff
        void loadLists(boolean forceUpdate);
        void showListDetail(@NonNull List requestList);
        void showUserSetting();
        void startVoiceService(String result);
        void loadUser(String userId);
        void updateList(List list);
        void deleteList(String listId);
        void deleteStuffByListId(String listId);
        void updateListGroup(ListGroup listGroup);
        void deleteListGroup(String listGroupId);
    }
}
