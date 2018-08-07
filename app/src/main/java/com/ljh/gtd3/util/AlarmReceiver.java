package com.ljh.gtd3.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ljh.gtd3.R;
import com.ljh.gtd3.service.NotifyService;
import com.ljh.gtd3.stuffDetail.StuffDetailActivity;

/**
 * 设置通知
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //获得intent参数
        String userId = intent.getStringExtra("USERID");
        String stuffId = intent.getStringExtra("STUFFID");
        String stuffName = intent.getStringExtra("STUFFNAME");
        String stuffIntroduce = intent.getStringExtra("STUFFINTRODUCE");

        Log.d(TAG, "onReceive: ");
        //接受广播
        if (intent.getAction().equals("NOTIFICATION")) {
            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            //通知点击事件->跳转到stuffDetailActivity.class
            Intent toStuffDetailActivityIntent = new Intent(context, StuffDetailActivity.class);
            toStuffDetailActivityIntent.putExtra("STUFFID", stuffId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, toStuffDetailActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notify = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
//                   .setLargeIcon(largeIcon)
                    .setTicker("您有新通知，请注意查收！")
                    .setContentTitle(stuffName)
                    .setContentText(stuffIntroduce)
                    .setContentIntent(pendingIntent).setNumber(1).getNotification();
            notify.flags |= Notification.FLAG_AUTO_CANCEL; // FL
            //发送通知
            manager.notify(1, notify);
            //发送完通知，开启后台通知服务。
            Intent intent2 = new Intent(context, NotifyService.class);
            intent2.putExtra("USERID", userId);
            context.startService(intent2);
        }
    }
}
