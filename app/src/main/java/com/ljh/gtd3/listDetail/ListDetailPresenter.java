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

    private List mList;

    public ListDetailPresenter(ListsRepository mListsRepository, TasksRepository mTasksRepository, ListDetailContract.View mListDetailView,List list) {
        this.mListsRepository = mListsRepository;
        this.mTasksRepository = mTasksRepository;
        this.mListDetailView = mListDetailView;
        this.mList = list;
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
        if(mList != null) {
            mTasksRepository.getTasksByListId(mList.getId(), new TasksDataSource.GetTasksCallBack() {
                @Override
                public void onTasksLoaded(java.util.List<Task> tasks, String message) {
                    if(!tasks.isEmpty()) {
                        if(!mListDetailView.isActive()) {
                            return;
                        }
                        mListDetailView.setLoadingIndicator(false);   //取消刷新指示
                        mListDetailView.showList(mList);                //加载List的信息
                        mListDetailView.showAllTasks(tasks);             //加载tasks
                    }
                }

                @Override
                public void onTasksFail(String message) {
                    mListDetailView.showList(mList);        //加载List的信息
                    mListDetailView.setLoadingIndicator(false);
//                        mListDetailView.setLoadingTasksError();
                    mListDetailView.showNoTasks();
                }
            });
        }else {
            mListDetailView.setLoadingIndicator(false);
            mListDetailView.setLoadingTasksError();
            mListDetailView.showNoTasks();
        }
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
        mListDetailView.showTaskDetail(requestTask);
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
                task.setList(mList);
                task.setGmtCreate(simpleDateFormat.format(new Date()));
                task.setGmtModified(simpleDateFormat.format(new Date()));
                task.setFinished(false);
                mTasksRepository.addTask(task);
                mListsRepository.updateTasksNum(task.getList_id());
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
            mListsRepository.subtractTasksNum(task.getList_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startVoiceService(String result) {
        mListDetailView.startVoiceService(result);
    }

}
