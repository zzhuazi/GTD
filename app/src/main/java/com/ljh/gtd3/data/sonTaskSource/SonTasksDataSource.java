package com.ljh.gtd3.data.sonTaskSource;


import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.SonTask;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public interface SonTasksDataSource {

    interface GetSonTasksCallBack{
        void onSonTasksLoaded(List<SonTask> sonTasks, String message);
        void onSonTasksFail(String message);
    }

    interface GetSonTaskCallBack{
        void onSonTaskLoaded(SonTask sonTask, String message);
        void onSonTaskFail(String message);
    }

    void getSonTask(@NonNull int sonTaskId, @NonNull GetSonTaskCallBack callBack);

    void getSonTasks( @NonNull int task, @NonNull GetSonTasksCallBack callBack);

    void addSonTask(@NonNull SonTask sonTask);

    void updateSonTask(@NonNull SonTask sonTask);

    void deleteSonTask(@NonNull int sonTaskId);

    void deleteSonTasks(@NonNull int taskId);
}
