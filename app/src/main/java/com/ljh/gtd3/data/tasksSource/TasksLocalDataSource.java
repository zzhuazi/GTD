package com.ljh.gtd3.data.tasksSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class TasksLocalDataSource implements TasksDataSource {

    private static final String TAG = TasksLocalDataSource.class.getSimpleName();
    private static TasksLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    //防止直接被实例化
    private TasksLocalDataSource(@NonNull AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
    }

    public static TasksLocalDataSource getInstance(@NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (TasksLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksLocalDataSource(appExecutors);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    //根据taskId查找Id
    @Override
    public void getTask(@NonNull final int taskId, @NonNull final GetTaskCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Task task = LitePal.find(Task.class, taskId);
                if(task == null) {
                    callBack.onTaskFail("小二丢失了该任务");
                }else {
                    callBack.onTaskLoaded(task, "小二为您呈现任务");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    //按创建顺序查找所有task
    @Override
    public void getAllTasks(@NonNull final GetTasksCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Task> tasks = LitePal.order("gmtCreate desc").find(Task.class);
                if(tasks.size() == 0) {
                    callBack.onTasksFail("该清单下没有任务");
                }else {
                    callBack.onTasksLoaded(tasks,"小二为您呈现任务");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getTasksByListId(@NonNull final int listId, @NonNull final GetTasksCallBack callBack) {
        Runnable runnable = new Runnable() {
            public void run() {
                List<Task> tasks = LitePal.where("list_id = ?", String.valueOf(listId)).find(Task.class);
                if(tasks.size() == 0) {
                    callBack.onTasksFail("该清单下没有任务");
                }else {
                    callBack.onTasksLoaded(tasks,"小二为您呈现任务");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getTasksByStartDate(@NonNull final String startDate, @NonNull final GetTasksCallBack callBack) {
        Runnable runnable = new Runnable() {
            public void run() {
                List<Task> tasks = LitePal.where("startDate = ?", startDate).order("gmtCreate desc").find(Task.class);
                if(tasks.size() == 0) {
                    callBack.onTasksFail("该清单下没有任务");
                }else {
                    callBack.onTasksLoaded(tasks,"小二为您呈现任务");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    //根据taskid删除单个任务
    @Override
    public void deleteTask(@NonNull final Task task) {
        deleteTask(task.getId());
    }

    @Override
    public void deleteTask(@NonNull final int taskId) {
        Runnable runnable = new Runnable() {
            public void run() {
                LitePal.delete(Task.class, taskId);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    //删除所有任务
    @Override
    public void deleteAllTasks() {
        Runnable runnable = new Runnable() {
            public void run() {
                LitePal.deleteAll(Task.class);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    //根据ListId删除任务
    @Override
    public void deleteTasksByListId(@NonNull final int listId) {
        Runnable runnable = new Runnable() {
            public void run() {
                LitePal.deleteAll(Task.class,"list_id = ?", String.valueOf(listId));
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    //更新task
    @Override
    public void updateTask(@NonNull final Task task) {
        Runnable runnable = new Runnable() {
            public void run() {
                task.update(task.getId());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    //添加task
    @Override
    public void addTask(@NonNull final Task task) {
//        Runnable runnable = new Runnable() {
//            public void run() {
//                getTask(task.getId(), new GetTaskCallBack() {
//                    @Override
//                    public void onTaskLoaded(Task task, String message) {
//                        updateTask(task);
//                    }
//
//                    @Override
//                    public void onTaskFail(String message) {
//                        task.save();
//                    }
//                });
//            }
//        };
        task.save();
//        mAppExecutors.diskIO().execute(runnable);
    }
}
