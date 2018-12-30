package com.ljh.gtd3.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ljh.gtd3.R;
import com.ljh.gtd3.data.entity.Task;
import com.ljh.gtd3.service.NotifyService;
import com.ljh.gtd3.taskDetail.TaskDetailActivity;

import okhttp3.internal.http.RetryAndFollowUpInterceptor;

/**
 * 设置通知
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //获得intent参数
        try {
            Bundle bundle = intent.getExtras();
            String taskString = (String) bundle.get("TASK");
            Task task = new Gson().fromJson(taskString, Task.class);
            if(task.getName().isEmpty() || task.getName().equals("")) {  //如果传回的task是一个空值，则重新开启通知服务，并退出当前方法。
                //发送完通知，开启后台通知服务。
                Intent intent2 = new Intent(context, NotifyService.class);
                context.startService(intent2);
                return;
            }
            int taskId = task.getId();
            String taskName = task.getName();
            String taskIntroduce = task.getIntroduce();
            //接受广播
            if (intent.getAction().equals("NOTIFICATION")) {
                //通知点击事件->跳转到taskDetailActivity.class
                Intent toTaskDetailActivityIntent = new Intent(context, TaskDetailActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("TASK", new Gson().toJson(task));
                toTaskDetailActivityIntent.putExtras(bundle1);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, taskId, toTaskDetailActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                String id = "my_channel_01";
                String name = "渠道名字";
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                Notification notification = null;
                //当 API >= 26时，发送通知需要设置Channel
                if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
                    notificationManager.createNotificationChannel(channel);
                    notification = new Notification.Builder(context)
                            .setChannelId(id)
                            .setTicker("您有新任务喔！")
                            .setContentTitle(taskName)
                            .setContentText(taskIntroduce)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .build();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                }else {
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                            .setContentTitle(taskName)
                            .setTicker("您有新任务喔！")
                            .setContentText(taskIntroduce)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setOngoing(true)
                            .setContentIntent(pendingIntent);
                    notification = notificationBuilder.build();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                }
                notificationManager.notify(taskId, notification);

                //发送完通知，开启后台通知服务。
                Intent intent2 = new Intent(context, NotifyService.class);
                context.startService(intent2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
