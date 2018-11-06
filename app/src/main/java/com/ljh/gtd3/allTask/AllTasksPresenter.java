package com.ljh.gtd3.allTask;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksDataSource;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.util.TasksFilterType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/13.
 */

public class AllTasksPresenter implements AllTasksContract.Presenter {
    public static final String TAG = AllTasksPresenter.class.getSimpleName();
    private final ListsRepository mListsRepository;
    private final TasksRepository mTasksRepository;
    private final AllTasksContract.View mAllTaskView;
    private boolean mFirstLoad = false;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    public AllTasksPresenter(ListsRepository mListsRepository, TasksRepository mTasksRepository, AllTasksContract.View mAllTaskView) {
        this.mListsRepository = mListsRepository;
        this.mTasksRepository = mTasksRepository;

        this.mAllTaskView = mAllTaskView;

        mAllTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks();
    }

    @Override
    public void showAddTask() {
        Map<String, String> map = new HashMap<>();
        mAllTaskView.showAddTask(map);
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        Log.d(TAG, "completeTask: " + completedTask.getFinished());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        completedTask.setFinished(true);
        completedTask.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mTasksRepository.updateTask(completedTask);
            loadTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        Log.d(TAG, "activateTask: " + activeTask.getFinished());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        activeTask.setFinished(false);
        activeTask.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mTasksRepository.updateTask(activeTask);
            loadTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showTaskDetail(@NonNull Task requestTask) {
        mAllTaskView.showTaskDetail(requestTask);
    }


//    @Override
//    public void addTask(final Task task) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        task.setPriority(0);
//        task.setGmtCreate(simpleDateFormat.format(new Date()));
//        task.setGmtModified(simpleDateFormat.format(new Date()));
//        task.setFinished(false);
//        mTasksRepository.addTask(task);
//    }


    @Override
    public void deleteTask(final Task task) {
        Log.d(TAG, "deleteTask: ");
        try {
            mTasksRepository.deleteTask(task);
            mListsRepository.subtractTasksNum(task.getList_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startVoiceService(String result) {
        mAllTaskView.startVoiceService(result);
    }

    //设置分类类别
    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    //获得分类类别
    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }


    public void loadTasks() {
        //显示
        mAllTaskView.setLoadingIndicator(true);
        mTasksRepository.getAllTasks(new TasksDataSource.GetTasksCallBack() {
            @Override
            public void onTasksLoaded(List<Task> tasks, String message) {
                //获取所有任务成功
                getTasksSuccess(tasks);
            }

            @Override
            public void onTasksFail(String message) {
                //获取所有任务失败
                getTasksFail(message);
            }
        });

    }

    //获取所有任务失败
    private void getTasksFail(String message) {
        mAllTaskView.setLoadingIndicator(false);
        mAllTaskView.setLoadingTasksError();
        mAllTaskView.showNoTasks();
        mAllTaskView.showToast(message);
    }

    //获取所有任务成功
    private void getTasksSuccess(List<Task> tasks) {
        if (tasks.size() == 0) {
            mAllTaskView.setLoadingIndicator(false);
            mAllTaskView.showNoTasks();
        } else {
            List<Task> tasksToShow = new ArrayList<>();
            //将tasks按照类别分类
            for (Task task : tasks) {
                switch (mCurrentFiltering) {
                    case ALL_TASKS:
                        tasksToShow.add(task);
                        break;
                    case ACTIIVE_TASKS:
                        if (!task.getFinished()) {
                            tasksToShow.add(task);
                        }
                        break;
                    case COMPLETED_TASKS:
                        if (task.getFinished()) {
                            tasksToShow.add(task);
                        }
                        break;
                    default:
                        tasksToShow.add(task);
                        break;
                }
            }
            //判断fragment是否添加，若未添加则退出
            if (!mAllTaskView.isActive()) {
                return;
            }
            //取消下拉刷新的刷新效果
            mAllTaskView.setLoadingIndicator(false);
            Log.d(TAG, "getTasksSuccess: tasksToShow::::::::::::::" + tasksToShow.size());
            //判断要显示的tasks是否为空
            if (tasksToShow.isEmpty()) {
                mAllTaskView.showNoTasks();
                switch (mCurrentFiltering) {
                    case ACTIIVE_TASKS:
                        mAllTaskView.showToast("没有未完成的任务");
                        break;
                    case COMPLETED_TASKS:
                        mAllTaskView.showToast("没有已完成的任务");
                        break;
                }
            } else {
                mAllTaskView.showAllTasks(tasksToShow);
            }
        }
    }
}
