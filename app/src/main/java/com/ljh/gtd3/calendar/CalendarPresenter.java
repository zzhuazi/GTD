package com.ljh.gtd3.calendar;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ljh.gtd3.data.ListsSource.ListsDataSource;
import com.ljh.gtd3.data.ListsSource.ListsRepository;
import com.ljh.gtd3.data.tasksSource.TasksDataSource;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.data.entity.List;
import com.ljh.gtd3.data.entity.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/29.
 */

public class CalendarPresenter implements CalendarContract.Presenter {
    public static final String TAG = CalendarContract.class.getSimpleName();
    private final ListsRepository mListsRepository;
    private final TasksRepository mTasksRepository;
    private CalendarContract.View mCalendarView;


    public CalendarPresenter( ListsRepository mListsRepository, TasksRepository mTasksRepository, CalendarContract.View mCalendarView) {
        this.mListsRepository = mListsRepository;
        this.mTasksRepository = mTasksRepository;
        this.mCalendarView = mCalendarView;
        mCalendarView.setPresenter(this);
    }

    @Override
    public void start() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        loadTasks(simpleDateFormat.format(new Date()));
        setSchemeDate();
    }

    @Override
    public void showAddTask() {
        Map<String, String> map = new HashMap<>();
        mCalendarView.showAddTask(map);
    }

    //根据点击日期加载tasks
    @Override
    public void loadTasks(final String clickTime) {
        mTasksRepository.getAllTasks(new TasksDataSource.GetTasksCallBack() {
            @Override
            public void onTasksLoaded(java.util.List<Task> tasks, String message){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try{
                    java.util.List<Task> taskList = new ArrayList<>();
                    for (Task task : tasks){
                        if(task.getStartTime() != null && task.getEndTime() != null && !task.getStartTime().equals("null") && !task.getEndTime().equals("null")) {   //如果有开始日期和结束日期就把这两个日期中的所有日期添加红点
                            Date start = simpleDateFormat.parse(task.getStartTime());
                            Date end = simpleDateFormat.parse(task.getEndTime());
                            Date date = simpleDateFormat.parse(clickTime);
                            if(date.after(start) && date.before(end)) {
                                taskList.add(task);
                            }
                        }
                        if(task.getStartTime() != null && !task.getStartTime().equals("null")) {
                            if(task.getStartTime().startsWith(clickTime.substring(0,10))) {
                                taskList.add(task);
                            }
                        }
                        if(task.getEndTime() != null && !task.getEndTime().equals("null")) {
                           if(task.getEndTime().startsWith(clickTime.substring(0,10))) {
                               taskList.add(task);
                           }
                        }
                    }
                    if(taskList.size() == 0) {
                        mCalendarView.showNoTasks();
                    }else {
                        mCalendarView.showAllTasks(taskList);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onTasksFail(String message) {
                Log.d(TAG, "onTasksFail: ");
                mCalendarView.showNoTasks();
            }
        });
//        mTasksRepository.getTasksByStartDate(mUserId, clickTime, new TasksDataSource.GetTasksCallBack() {
//            @Override
//            public void onTasksLoaded(java.util.List<Task> Tasks, String message) {
//                if (Tasks.size() == 0) {
//                    mCalendarView.showNoTasks();
//                }
//                mCalendarView.showAllTasks(Tasks);
//            }
//
//            @Override
//            public void onTasksFail(String message) {
//                mCalendarView.showNoTasks();
//            }
//        });
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        Log.d(TAG, "completeTask: " + completedTask.getFinished());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        completedTask.setFinished(true);
        completedTask.setGmtModified(simpleDateFormat.format(new Date()));
        try {
            mTasksRepository.updateTask(completedTask);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showTaskDetail(@NonNull Task requestTask) {
        mCalendarView.showTaskDetail(requestTask);
    }

    @Override
    public void addTask(final Task task) {
        mListsRepository.GetLists( new ListsDataSource.GetListsCallBack() {
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
                mListsRepository.updateTasksNum(task.getId());
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
        mCalendarView.startVoiceService(result);
    }

    @Override
    public void setSchemeDate() {
        mTasksRepository.getAllTasks(new TasksDataSource.GetTasksCallBack() {
            @Override
            public void onTasksLoaded(java.util.List<Task> tasks, String message) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                Map<String, com.haibin.calendarview.Calendar> map = new HashMap<>();
                try{
                    for (Task task : tasks){
                        boolean startTimeNotEmpty =  task.getStartTime() != null && !task.getStartTime().equals("null") && !task.getStartTime().equals("");
                        if(startTimeNotEmpty) {   //如果有开始日期不为空，则日期添加标记
                            Date start = simpleDateFormat.parse(task.getStartTime());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(start);
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH) + 1;
                            int date = calendar.get(Calendar.DATE);
                            map.put(getSchemeCalendar(year, month, date).toString(),
                                    getSchemeCalendar(year, month, date));
                        }
                    }
                    mCalendarView.setSchemeDate(map);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onTasksFail(String message) {

            }
        });
    }

    public com.haibin.calendarview.Calendar getSchemeCalendar(int year, int month, int day) {
        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
//        calendar.setSchemeColor(0xFF40db25);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme("假");
        return calendar;
    }
}
