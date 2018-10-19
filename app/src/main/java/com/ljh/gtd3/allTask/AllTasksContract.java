package com.ljh.gtd3.allTask;

import android.support.annotation.NonNull;

import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.Task;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/13.
 */

public interface AllTasksContract {
    interface View extends BaseView<Presenter>{
        void setLoadingIndicator(boolean active);   //正在加载指示
        void setLoadingTasksError();  //加载错误
        void showNoTasks();  //显示没有Tasks
        boolean isActive();  //是否加载Fragment
        void showToast(String message);  //显示toast

        void showAllTasks(List<Task> Tasks);  //显示所有Tasks

        void showAddTask(Map<String, String > map);   //显示添加Task
        void showTaskDetail(Task task);    //显示TaskDetial

        void startVoiceService(String result);
    }

    interface Presenter extends BasePresenter{
        void loadTasks();           //加载Tasks

        void showAddTask();         //显示加载Task的页面
        void showTaskDetail(@NonNull Task requestTask);  //点击任务

//        void addTask(Task task);         //添加Task
        void completeTask(@NonNull Task completedTask);  //完成任务
        void activateTask(@NonNull Task activeTask);     //激活任务
        void deleteTask(Task task);      //删除Task

        void startVoiceService(String result);    //开启语音服务
    }
}
