package com.ljh.gtd3.taskDetail;

import android.util.Log;

import com.ljh.gtd3.data.sonTaskSource.SonTasksDataSource;
import com.ljh.gtd3.data.sonTaskSource.SonTasksRepository;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.data.entity.SonTask;
import com.ljh.gtd3.data.entity.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 */

public class TaskDetailPresenter implements TaskDetailContract.Presenter {
    public static final String TAG = TaskDetailPresenter.class.getSimpleName();

    private final ListsRepository mListsRepository;

    private final TasksRepository mTasksRepository;

    private final SonTasksRepository mSonTasksRepository;

    private TaskDetailContract.View mTaskDetailView;

    private Task mTask;

    public TaskDetailPresenter(ListsRepository mListsRepository, TasksRepository mTasksRepository, SonTasksRepository mSonTasksRepository, TaskDetailContract.View mTaskDetailView, Task task) {
        this.mListsRepository = mListsRepository;
        this.mTasksRepository = mTasksRepository;
        this.mSonTasksRepository = mSonTasksRepository;
        this.mTaskDetailView = mTaskDetailView;
        this.mTask = task;
        this.mTaskDetailView.setPresenter(this);
    }

    //加载Task的数据
    @Override
    public void start() {
        if(mTask == null) {
            Log.d(TAG, "start: task == null");
            mTaskDetailView.showAllTasks();
        }else {
            Log.d(TAG, "start: " + mTask);
            mListsRepository.GetList(mTask.getList_id(), new ListsDataSource.GetListCallBack() {
                @Override
                public void onListLoaded(com.ljh.gtd3.data.entity.List list, String message) {
                    mTaskDetailView.showTask(mTask, list);
                }

                @Override
                public void onListFail(String message) {

                }
            });
            showAllSonTask();
        }
    }

    @Override
    public void showLists() {
        mListsRepository.GetLists(new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(List<com.ljh.gtd3.data.entity.List> lists, String message) {
                mTaskDetailView.showLists(lists);
            }

            @Override
            public void onListsFail(String message) {
                mTaskDetailView.showToast(message);
            }
        });
    }

    @Override
    public void showAllTasks() {
        mTaskDetailView.showAllTasks();
    }

    @Override
    public void updateTask(Task task) {
        task.setId(mTask.getId());
        mTasksRepository.updateTask(task);
        mTaskDetailView.showAllTasks();
    }

    @Override
    public void showAllSonTask() {
        mSonTasksRepository.getSonTasks(mTask.getId(), new SonTasksDataSource.GetSonTasksCallBack() {
            @Override
            public void onSonTasksLoaded(List<SonTask> sonTasks, String message) {
                mTaskDetailView.showAllSonTask(sonTasks);
            }

            @Override
            public void onSonTasksFail(String message) {
                List<SonTask> sonTasks = new ArrayList<>();
                mTaskDetailView.showAllSonTask(sonTasks);
            }
        });
    }

    @Override
    public void addSonTask(SonTask sonTask) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //setTask
        sonTask.setGmtCreate(simpleDateFormat.format(new Date()));
        sonTask.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mSonTasksRepository.addSonTask(sonTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completeSonTask(SonTask sonTask) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sonTask.setFinished(true);
        sonTask.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mSonTasksRepository.updateSonTask(sonTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activateSonTask(SonTask sonTask) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sonTask.setFinished(false);
        sonTask.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mSonTasksRepository.updateSonTask(sonTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSonTask(SonTask sonTask) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sonTask.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mSonTasksRepository.updateSonTask(sonTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSonTask(int sonTaskId) {
        try {
            mSonTasksRepository.deleteSonTask(sonTaskId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
