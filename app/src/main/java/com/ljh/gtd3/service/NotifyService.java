package com.ljh.gtd3.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.data.tasksSource.TasksLocalDataSource;
import com.ljh.gtd3.data.tasksSource.TasksRepository;
import com.ljh.gtd3.util.AlarmReceiver;
import com.ljh.gtd3.data.tasksSource.TasksDataSource;
import com.ljh.gtd3.util.AppExecutors;
import com.ljh.gtd3.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 通知提醒
 */
public class NotifyService extends Service {
    private static final String TAG = NotifyService.class.getSimpleName();

    public NotifyService() {
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(intent == null) {
                    Log.d(TAG, "run: intent is null.");
                    stopSelf();
                    return;
                }
                Log.d(TAG, "run: 通知服务开启" );
                AppExecutors appExecutors = new AppExecutors();
                TasksRepository tasksRepository = TasksRepository.getInstance(TasksLocalDataSource.getInstance(appExecutors));
                //查找该用户下所有材料
                tasksRepository.getAllTasks(new TasksDataSource.GetTasksCallBack() {
                    @Override
                    public void onTasksLoaded(List<Task> tasks, String message) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            //获得通知的时间列表
                            for (Iterator iterator = tasks.iterator(); iterator.hasNext();){
                                Task task = (Task) iterator.next();
                                if(task.getStartTime() == null) {  //当startTime为空时，将Task移除
                                    iterator.remove();
                                }else if(task.getStartTime().equals("null") || task.getStartTime().equals("")) {  //当格式不正确时移除
                                    iterator.remove();
                                }else if(!DateUtil.isRightDateStr(task.getStartTime())) {
                                    iterator.remove();
                                } else if(simpleDateFormat.parse(task.getStartTime()).getTime() < new Date().getTime()) {  //当时间小于当前时间时移除
                                    iterator.remove();
                                }
                            }
                            //冒泡排序，将日期从小到大排
                            boolean exchange;
                            Task startTimeTask;
                            for (int i = 0; i< tasks.size(); i++){
                                exchange = false;
                                for (int j = tasks.size()-1 ; j > i; j--){
                                    Date date = simpleDateFormat.parse(tasks.get(j).getStartTime());
                                    Date date1 = simpleDateFormat.parse(tasks.get(j - 1).getStartTime());
                                    if(date.before(date1)) {
                                        startTimeTask = tasks.get(j);
                                        tasks.set(j,tasks.get(j-1));
                                        tasks.set(j-1, startTimeTask);
                                        exchange = true;
                                    }
                                }
                                if(!exchange) {
                                    break;
                                }
                            }
                            for (Task Task: tasks){
                                Log.d(TAG, "notifyService Tasks :" + Task.getStartTime());
                            }
                            if(tasks.size() != 0) {
                                long triggerAtMillis = simpleDateFormat.parse(tasks.get(0).getStartTime()).getTime();
                                Log.d(TAG, "onCreate: " + triggerAtMillis);
                                Intent intent1 = new Intent(NotifyService.this, AlarmReceiver.class);
                                intent1.putExtra("TASKID", tasks.get(0).getId());
                                intent1.putExtra("TASKNAME", tasks.get(0).getName());
                                intent1.putExtra("TASKINTRODUCE", tasks.get(0).getIntroduce());
                                intent1.setAction("NOTIFICATION");
                                //提醒的行动，发送广播
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(NotifyService.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                                //设置提醒时间
                                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                int type = AlarmManager.RTC_WAKEUP;
                                manager.set(type, triggerAtMillis, pendingIntent);
                                stopSelf();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onTasksFail(String message) {

                    }
                });
            }
        };
        runnable.run();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
