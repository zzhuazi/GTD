package com.ljh.gtd3.calendar;

import android.support.annotation.NonNull;

import com.haibin.calendarview.Calendar;
import com.ljh.gtd3.BasePresenter;
import com.ljh.gtd3.BaseView;
import com.ljh.gtd3.data.entity.Task;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public interface CalendarContract {

    interface View extends BaseView<Presenter>{
        void setLoadingTasksError();  //加载错误
        void showAllTasks(List<Task> tasks);  //显示所有Tasks
        void showAddTask(Map<String, String > map);   //显示加载Task
        void showTaskDetail(Task task);    //显示TaskDetial
        void showNoTasks();  //显示没有Tasks
        boolean isActive();  //是否加载Fragment
        void showToast(String message);  //显示toast
        void startVoiceService(String result);

        void setSchemeDate(Map<String, Calendar> map);
    }

    interface Presenter extends BasePresenter{
        void loadTasks(String startTime);
        void completeTask(@NonNull Task completedTask);
        void activateTask(@NonNull Task activeTask);
        void addTask(Task task); //添加Task(处理Affair中的情况)
        void deleteTask(Task task);

        void showTaskDetail(@NonNull Task requestTask);
        void showAddTask();
        void startVoiceService(String result);

        void setSchemeDate(); //设置标记
    }
}
