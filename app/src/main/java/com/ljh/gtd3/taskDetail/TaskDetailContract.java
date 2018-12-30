package com.ljh.gtd3.taskDetail;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.SonTask;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Task;

/**
 * Created by Administrator on 2018/3/16.
 */

public interface TaskDetailContract {
    interface View extends BaseView<Presenter> {
        void showTask(Task task, List list); //显示任务信息
        void showLists(java.util.List<List> lists);  //选中清单名时，弹出对话框
        void showStartTime(); //选中开始时间时，弹出对话框，并将时间显示
        void showEndTime(); //选中结束时间时，弹出对话框，并将时间显示
        void showPriority(); //选中优先级时，弹出对话框，并设计优先级图标背景颜色
        void showAllSonTask(java.util.List<SonTask> sonTasks); //显示事务
        void showToast(String message);
        
        void showAllTasks();//显示allTasks页面
        void addSonTask(SonTask sonTask, int pos);
    }

    interface Presenter extends BasePresenter {
        void showLists(); //选中清单名时，弹出对话框
        void showAllTasks(); //显示allTasks页面
        void updateTask(Task task); //更新Task(处理SonTask中的情况)

        void showAllSonTask(); //显示事务
        void addSonTask(SonTask sonTask);
        void completeSonTask(SonTask sonTask);
        void activateSonTask(SonTask sonTask);
        void updateSonTask(SonTask sonTask);
        void deleteSonTask(int SonTaskId);

        void replaceTask(Task task);  //更新presenter中的task
    }
}
