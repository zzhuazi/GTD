package com.ljh.gtd3.listDetail;

import android.support.annotation.NonNull;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Task;

import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public interface ListDetailContract {
    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);   //正在加载指示
        void setLoadingTasksError();  //加载错误
        void showList(List list);    //加载list
        void showAllTasks( java.util.List<Task> tasks);  //显示所有Tasks
        void showAddTask(Map<String, String > map);   //显示加载Task
        void showTaskDetail(Task task);    //显示TaskDetial
        void showNoTasks();  //显示没有Tasks
        boolean isActive();  //是否加载Fragment
        void showToast(String message);  //显示toast
        void startVoiceService(String result);
        
    }

    interface Presenter extends BasePresenter{
        void showAddTask();
        void loadTasks(boolean forceUpdate);
        void completeTask(@NonNull Task completedTask);
        void activateTask(@NonNull Task activeTask);
        void showTaskDetail(@NonNull Task requestTask);
        void addTask(Task task); //添加Task(处理Affair中的情况)
        void deleteTask(Task task);
        void startVoiceService(String result);
    }
}
