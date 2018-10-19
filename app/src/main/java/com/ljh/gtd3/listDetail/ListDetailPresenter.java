package com.ljh.gtd3.listDetail;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksDataSource;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class ListDetailPresenter implements ListDetailContract.Presenter{
    public static final String TAG = ListDetailPresenter.class.getSimpleName();
    
    private final ListsRepository mListsRepository;
    private final TasksRepository mTasksRepository;
    private final ListDetailContract.View mListDetailView;

    private int mListId;

    public ListDetailPresenter(ListsRepository mListsRepository, TasksRepository mTasksRepository, ListDetailContract.View mListDetailView,int mListId) {
        this.mListsRepository = mListsRepository;
        this.mTasksRepository = mTasksRepository;
        this.mListDetailView = mListDetailView;
        this.mListId = mListId;
        mListDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void showAddTask() {
        Map<String, String> map = new HashMap<>();
        mListDetailView.showAddTask(map);
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        mListDetailView.setLoadingIndicator(true);
        mListsRepository.GetList(mListId, new ListsDataSource.GetListCallBack() {
            @Override
            public void onListLoaded(final List list, String message) {
                mTasksRepository.getTasksByListId(mListId, new TasksDataSource.GetTasksCallBack() {
                    @Override
                    public void onTasksLoaded(java.util.List<Task> tasks, String message) {
                        if(!mListDetailView.isActive()) {
                            return;
                        }
                        mListDetailView.setLoadingIndicator(false);
                        mListDetailView.showAllTasks(list, tasks);
                    }

                    @Override
                    public void onTasksFail(String message) {
                        mListDetailView.showAllTasks(list, null);
                        mListDetailView.setLoadingIndicator(false);
//                        mListDetailView.setLoadingTasksError();
                        mListDetailView.showNoTasks();
                    }
                });
            }

            @Override
            public void onListFail(String message) {
                mListDetailView.setLoadingIndicator(false);
                mListDetailView.setLoadingTasksError();
                mListDetailView.showNoTasks();
            }
        });
    }


    @Override
    public void completeTask(@NonNull Task completedTask) {
        Log.d(TAG, "completeTask: " + completedTask.getFinished());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        completedTask.setFinished(true);
        completedTask.setGmtModified(simpleDateFormat.format(new Date()));
        try{
            mTasksRepository.updateTask(completedTask);
        }catch (Exception e){
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void showTaskDetail(@NonNull Task requestTask) {
        mListDetailView.showTaskDetail(requestTask.getId());
    }

    @Override
    public void addTask(final Task task) {
        mListsRepository.GetLists(new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(java.util.List<List> lists, String message) {
                Log.d(TAG, "onListsLoaded: list.size():" + lists.size());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                task.setPriority(0);
                //setList
                task.setGmtCreate(simpleDateFormat.format(new Date()));
                task.setGmtModified(simpleDateFormat.format(new Date()));
                task.setFinished(false);
                mTasksRepository.addTask(task);
//                mListsRepository.updateTasksNum(task.getId());
            }

            @Override
            public void onListsFail(String message) {

            }
        });
    }

    @Override
    public void deleteTask(final Task task) {
        Log.d(TAG, "deleteTask: ");
        try {
            mTasksRepository.deleteTask(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startVoiceService(String result) {
        mListDetailView.startVoiceService(result);
    }

}
