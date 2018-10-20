package com.ljh.gtd3.addTask;

import com.ljh.gtd3.data.sonTaskSource.SonTasksRepository;
import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.data.entity.SonTask;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/15.
 */

public class AddTaskPresenter implements AddTaskContract.Presenter{
    public static final String TAG = AddTaskPresenter.class.getSimpleName();

    private final ListsRepository mListsRepository;

    private final TasksRepository mTasksRepository;

    private final SonTasksRepository mSonTasksRepository;

    private final AddTaskContract.View mAddTaskView;


    public AddTaskPresenter(ListsRepository mListsRepository, TasksRepository mTasksRepository, SonTasksRepository mSonTasksRepository, AddTaskContract.View mAddTaskView) {
        this.mListsRepository = mListsRepository;
        this.mTasksRepository = mTasksRepository;
        this.mSonTasksRepository = mSonTasksRepository;
        this.mAddTaskView = mAddTaskView;
        this.mAddTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        mListsRepository.GetLists(new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(List<com.ljh.gtd3.data.entity.List> lists, String message) {
                com.ljh.gtd3.data.entity.List list = lists.get(lists.size()-1);
                mAddTaskView.showListName(list);
            }

            @Override
            public void onListsFail(String message) {

            }
        });
    }

    //显示所有清单
    @Override
    public void showLists() {
        mListsRepository.GetLists(new ListsDataSource.GetListsCallBack() {
            @Override
            public void onListsLoaded(List<com.ljh.gtd3.data.entity.List> lists, String message) {
                mAddTaskView.showLists(lists);
            }

            @Override
            public void onListsFail(String message) {
                mAddTaskView.showToast(message);
            }
        });
    }

    @Override
    public void showAllTasks() {
        //显示所有Task页面
        mAddTaskView.showAllTasks();
    }

    /**
     * 添加材料
     * @param task
     */
    @Override
    public void addTask(Task task) {
        try{
            //默认startTime and endTime 格式都是正确的
            boolean isRightStartTime = true;
            boolean isRightEndTime = true;
            //如果startTime不为空，且格式不正确，则将isRightStartTime置为错误
            if(task.getStartTime() != null && !DateUtil.isRightDateStr(task.getStartTime())) {
                isRightStartTime = false;
            }
            //如果endTime不为空，且格式不正确，则将isRightEndTime置为错误
            if(task.getEndTime() != null && !DateUtil.isRightDateStr(task.getEndTime())) {
                isRightEndTime = false;
            }
            //当startTime and EndTime格式都正确的时候，添加材料
            if(isRightStartTime && isRightEndTime) {
                mTasksRepository.addTask(task);
                mListsRepository.updateTasksNum(task.getList_id());
            }else { //否则提示添加失败
                mAddTaskView.showToast("日期格式错误，添加材料失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            mAddTaskView.showToast("日期格式错误，添加材料失败");
        }
    }

    @Override
    public void addSonTask(Task task, List<SonTask> sonTasks) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SonTask sonTask : sonTasks){
            sonTask.setTask(task);
            sonTask.setGmtCreate(simpleDateFormat.format(new Date()));
            sonTask.setGmtModified(simpleDateFormat.format(new Date()));
            mSonTasksRepository.addSonTask(sonTask);
        }
    }
}
