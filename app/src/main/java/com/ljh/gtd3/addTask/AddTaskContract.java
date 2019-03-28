package com.ljh.gtd3.addTask;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.SonTask;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Task;

/**
 * Created by Administrator on 2018/3/15.
 */

public interface AddTaskContract {
    interface View extends BaseView<Presenter>{
        void showListName(List list); //显示toolbar中的清单名称
        void showLists(java.util.List<List> lists);  //选中清单名时，弹出对话框
        void showStartTime(); //选中开始时间时，弹出对话框，并将时间显示
//        void showEndTime(); //选中结束时间时，弹出对话框，并将时间显示
        void showPriority(); //选中优先级时，弹出对话框，并设计优先级图标背景颜色
        void showAllSonTask(java.util.List<SonTask> sonTasks); //显示事务
        void showToast(String message);
        void showAllTasks();//显示allTasks页面
        void addSonTask(SonTask sonTask, int pos);
    }


    interface Presenter extends BasePresenter{
        void showLists(); //选中清单名时，弹出对话框
        void showAllTasks(); //显示allTasks页面
        void addTask(Task task); //添加Task(处理Affair中的情况)
        void addSonTask(Task task, java.util.List<SonTask> sonTasks);
    }
}
