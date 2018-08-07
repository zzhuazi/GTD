package com.ljh.gtd3.addStuff;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;

/**
 * Created by Administrator on 2018/3/15.
 */

public interface AddStuffContract {
    interface View extends BaseView<Presenter>{
        void showListName(List list); //显示toolbar中的清单名称
        void showLists(java.util.List<List> lists);  //选中清单名时，弹出对话框
        void showStartTime(); //选中开始时间时，弹出对话框，并将时间显示
        void showEndTime(); //选中结束时间时，弹出对话框，并将时间显示
        void showPriority(); //选中优先级时，弹出对话框，并设计优先级图标背景颜色
        void showAllAffair(java.util.List<Affair> affairs); //显示事务
        void showToast(String message);
        void showAllStuffs();//显示allStuffs页面
        void addAffair(Affair affair, int pos);
    }


    interface Presenter extends BasePresenter{
        void showLists(); //选中清单名时，弹出对话框
        void showAllStuffs(); //显示allStuffs页面
        void addStuff(Stuff stuff); //添加Stuff(处理Affair中的情况)
        void addAffair(java.util.List<Affair> affairs);
    }
}
