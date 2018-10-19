package com.ljh.gtd3.data.tasksSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Task;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public interface TasksDataSource {
    //获取Tasks回调
    interface GetTasksCallBack{
        void onTasksLoaded(List<Task> tasks, String message);
        void onTasksFail(String message);
    }

    //获取单个Task回调
    interface GetTaskCallBack{
        void onTaskLoaded(Task task, String message);
        void onTaskFail(String message);
    }

    void getTask( @NonNull int taskId, @NonNull GetTaskCallBack callBack);

    void getAllTasks(@NonNull GetTasksCallBack callBack);

    void getTasksByListId(@NonNull int listId, @NonNull GetTasksCallBack callBack);

    void getTasksByStartDate(@NonNull String startDate, @NonNull GetTasksCallBack callBack);

    void deleteTask(@NonNull Task task);

    void deleteTask(@NonNull int taskId);

    void deleteAllTasks();

    void deleteTasksByListId(@NonNull int listId);

    void updateTask(@NonNull Task task);

    void addTask(@NonNull Task task);
}
