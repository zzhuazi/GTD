package com.ljh.gtd3.data.tasksSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.util.AppExecutors;


import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class TasksRepository implements TasksDataSource {
    public static final String TAG = TasksRepository.class.getSimpleName();
    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksLocalDataSource;

    private AppExecutors mAppExecutors;

    private TasksRepository(@NonNull TasksDataSource TasksLocalDataSource) {
        mTasksLocalDataSource = TasksLocalDataSource;
        mAppExecutors = new AppExecutors();
    }

    public static TasksRepository getInstance(@NonNull TasksDataSource TasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(TasksLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void getTask(@NonNull int taskId, @NonNull final GetTaskCallBack callBack) {
        mTasksLocalDataSource.getTask(taskId, new GetTaskCallBack() {
            @Override
            public void onTaskLoaded(Task task, String message) {
                callBack.onTaskLoaded(task, message);
            }

            @Override
            public void onTaskFail(String message) {
                callBack.onTaskFail(message);
            }
        });
    }

    @Override
    public void getAllTasks(@NonNull final GetTasksCallBack callBack) {
        mTasksLocalDataSource.getAllTasks(new GetTasksCallBack() {
            @Override
            public void onTasksLoaded(List<Task> tasks, String message) {
                callBack.onTasksLoaded(tasks, message);
            }

            @Override
            public void onTasksFail(String message) {
                callBack.onTasksFail(message);
            }
        });
    }

    @Override
    public void getTasksByListId(@NonNull int listId, @NonNull final GetTasksCallBack callBack) {
        mTasksLocalDataSource.getTasksByListId(listId, new GetTasksCallBack() {
            @Override
            public void onTasksLoaded(List<Task> tasks, String message) {
                callBack.onTasksLoaded(tasks, message);
            }

            @Override
            public void onTasksFail(String message) {
                callBack.onTasksFail(message);
            }
        });
    }

    @Override
    public void getTasksByStartDate(@NonNull String startDate, @NonNull final GetTasksCallBack callBack) {
        mTasksLocalDataSource.getTasksByStartDate(startDate, new GetTasksCallBack() {
            @Override
            public void onTasksLoaded(List<Task> tasks, String message) {
                callBack.onTasksLoaded(tasks, message);
            }

            @Override
            public void onTasksFail(String message) {
                callBack.onTasksFail(message);
            }
        });
    }

    @Override
    public void deleteTask(@NonNull Task task) {
        deleteTask(task.getId());
    }

    @Override
    public void deleteTask(@NonNull int taskId) {
        //删除子任务?
//        SonTasksLocalDataSource.getInstance(mAppExecutors).deleteSonTasks(task.getId());
        //删除该任务
        mTasksLocalDataSource.deleteTask(taskId);
        //修改该list中任务的数量?
    }

    @Override
    public void deleteAllTasks() {
        mTasksLocalDataSource.deleteAllTasks();
    }

    @Override
    public void deleteTasksByListId(@NonNull int listId) {
        mTasksLocalDataSource.deleteTasksByListId(listId);
    }

    @Override
    public void updateTask(@NonNull Task task) {
        mTasksLocalDataSource.updateTask(task);
    }

    @Override
    public void addTask(@NonNull Task task) {
        mTasksLocalDataSource.addTask(task);
    }
}