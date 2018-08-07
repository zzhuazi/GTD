package com.ljh.gtd3.stuffDetail;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.Affair;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Stuff;

/**
 * Created by Administrator on 2018/3/16.
 */

public interface StuffDetailContract {
    interface View extends BaseView<Presenter> {
        void showStuff(Stuff stuff, List list); //显示材料信息
        void showLists(java.util.List<List> lists);  //选中清单名时，弹出对话框
        void showStartTime(); //选中开始时间时，弹出对话框，并将时间显示
        void showEndTime(); //选中结束时间时，弹出对话框，并将时间显示
        void showPriority(); //选中优先级时，弹出对话框，并设计优先级图标背景颜色
        void showAllAffair(java.util.List<Affair> affairs); //显示事务
        void showToast(String message);
        void showAllStuffs();//显示allStuffs页面
        void addAffair(Affair affair, int pos);
    }

    interface Presenter extends BasePresenter {
        void showLists(); //选中清单名时，弹出对话框
        void showAllStuffs(); //显示allStuffs页面
        void updateStuff(Stuff stuff); //更新Stuff(处理Affair中的情况)
        void showAllAffair(); //显示事务
        void addAffair(Affair affair);
        void completeAffair(Affair affair);
        void activateAffair(Affair affair);
        void updateAffair(Affair affair);
        void deleteAffair(String affairId);
    }
}
