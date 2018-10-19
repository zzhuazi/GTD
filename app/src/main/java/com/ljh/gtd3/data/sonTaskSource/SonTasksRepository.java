package com.ljh.gtd3.data.sonTaskSource;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.SonTask;
import com.ljh.gtd3.util.MyApplication;

import java.util.List;
import java.util.Queue;

/**
 * Created by Administrator on 2018/3/9.
 */

public class SonTasksRepository implements SonTasksDataSource {
    public static final String TAG = SonTasksRepository.class.getSimpleName();
    private static SonTasksRepository INSTANCE = null;

    private final SonTasksDataSource mSonTaskLocalDataSource;

    private SonTasksRepository(SonTasksDataSource mSonTaskLocalDataSource) {
        this.mSonTaskLocalDataSource = mSonTaskLocalDataSource;
    }

    public static SonTasksRepository getInstance(@NonNull SonTasksDataSource SonTaskLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new SonTasksRepository(SonTaskLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void getSonTask(@NonNull int sonTaskId, @NonNull final GetSonTaskCallBack callBack) {
        mSonTaskLocalDataSource.getSonTask(sonTaskId, new GetSonTaskCallBack() {
            @Override
            public void onSonTaskLoaded(SonTask sonTask, String message) {
                callBack.onSonTaskLoaded(sonTask, message);
            }

            @Override
            public void onSonTaskFail(String message) {
                callBack.onSonTaskFail(message);
            }
        });
    }

    @Override
    public void getSonTasks(@NonNull int taskId, @NonNull final GetSonTasksCallBack callBack) {
        mSonTaskLocalDataSource.getSonTasks(taskId, new GetSonTasksCallBack() {
            @Override
            public void onSonTasksLoaded(List<SonTask> sonTasks, String message) {
                callBack.onSonTasksLoaded(sonTasks, message);
            }

            @Override
            public void onSonTasksFail(String message) {
                callBack.onSonTasksFail(message);
            }
        });
    }

    @Override
    public void addSonTask(@NonNull SonTask sonTask) {
        mSonTaskLocalDataSource.addSonTask(sonTask);
    }

    @Override
    public void updateSonTask(@NonNull SonTask sonTask) {
        mSonTaskLocalDataSource.updateSonTask(sonTask);
    }

    @Override
    public void deleteSonTask(@NonNull int sonTaskId) {
        mSonTaskLocalDataSource.deleteSonTask(sonTaskId);
    }

    @Override
    public void deleteSonTasks(@NonNull int taskId) {
        mSonTaskLocalDataSource.deleteSonTasks(taskId);
    }
}
