package com.ljh.gtd3.data.sonTaskSource;

import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.SonTask;
import com.ljh.gtd3.util.AppExecutors;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public class SonTasksLocalDataSource implements SonTasksDataSource {
    private static SonTasksLocalDataSource INSTANCE;

    private AppExecutors mAppExecutors;

    private SonTasksLocalDataSource(AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    public static SonTasksLocalDataSource getInstance(@NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            INSTANCE = new SonTasksLocalDataSource(appExecutors);
        }
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }

    @Override
    public void getSonTask(@NonNull final int sonTaskId, @NonNull final GetSonTaskCallBack callBack) {
        Runnable runnable = new Runnable() {
            public void run() {
                SonTask sonTask = LitePal.find(SonTask.class, sonTaskId);
                if(sonTask == null){
                    callBack.onSonTaskFail("小二丢失了该子任务");
                } else {
                    callBack.onSonTaskLoaded(sonTask,"小二为您呈现子任务");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getSonTasks(@NonNull final int taskId, @NonNull final GetSonTasksCallBack callBack) {
        Runnable runnable = new Runnable() {
            public void run() {
                List<SonTask> sonTasks = LitePal.where("task_id = ?", String.valueOf(taskId)).find(SonTask.class);
                if(sonTasks.isEmpty()) {
                    callBack.onSonTasksFail("没有子任务");
                }else {
                    callBack.onSonTasksLoaded(sonTasks, "小二为您呈现子任务");
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addSonTask(@NonNull final SonTask sonTask) {
        Runnable runnable = new Runnable() {
            public void run() {
                sonTask.save();
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void updateSonTask(@NonNull final SonTask sonTask) {
        Runnable runnable = new Runnable() {
            public void run() {
                sonTask.update(sonTask.getId());
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteSonTask(@NonNull final int sonTaskId) {
        Runnable runnable = new Runnable() {
            public void run() {
                LitePal.delete(SonTask.class, sonTaskId);
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteSonTasks(@NonNull final int taskId) {
        Runnable runnable = new Runnable() {
            public void run() {
                LitePal.deleteAll(SonTask.class, "task_id = ?", String.valueOf(taskId));
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
