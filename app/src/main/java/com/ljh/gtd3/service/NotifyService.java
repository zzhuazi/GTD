package com.ljh.gtd3.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ljh.gtd3.util.AlarmReceiver;
import com.ljh.gtd3.data.StuffsSource.StuffsDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsLocalDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.StuffsSource.remote.StuffsRemoteDataSource;
import com.ljh.gtd3.data.entity.Notification;
import com.ljh.gtd3.data.entity.Stuff;
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
    private static final String TAG = Notification.class.getSimpleName();

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
                final String userId = intent.getStringExtra("USERID");
                Log.d(TAG, "run: 通知服务开启。 用户id为：：：" + userId);
                AppExecutors appExecutors = new AppExecutors();
                StuffsRepository stuffsRepository = StuffsRepository.getInstance(StuffsLocalDataSource.getInstance(appExecutors), StuffsRemoteDataSource.getInstance(appExecutors));
                //查找该用户下所有材料
                stuffsRepository.getAllStuffs(userId, new StuffsDataSource.GetStuffsCallBack() {
                    @Override
                    public void onStuffsLoaded(List<Stuff> stuffs, String message) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            //获得通知的时间列表
                            for (Iterator iterator = stuffs.iterator(); iterator.hasNext();){
                                Stuff stuff = (Stuff) iterator.next();
                                if(stuff.getStartTime() == null) {  //当startTime为空时，将stuff移除
                                    iterator.remove();
                                }else if(stuff.getStartTime().equals("null") || stuff.getStartTime().equals("")) {  //当格式不正确时移除
                                    iterator.remove();
                                }else if(!DateUtil.isRightDateStr(stuff.getStartTime())) {
                                    iterator.remove();
                                } else if(simpleDateFormat.parse(stuff.getStartTime()).getTime() < new Date().getTime()) {  //当时间小于当前时间时移除
                                    iterator.remove();
                                }
                            }
                            //冒泡排序，将日期从小到大排
                            boolean exchange;
                            Stuff startTimeStuff;
                            for (int i = 0; i< stuffs.size(); i++){
                                exchange = false;
                                for (int j = stuffs.size()-1 ; j > i; j--){
                                    Date date = simpleDateFormat.parse(stuffs.get(j).getStartTime());
                                    Date date1 = simpleDateFormat.parse(stuffs.get(j - 1).getStartTime());
                                    if(date.before(date1)) {
                                        startTimeStuff = stuffs.get(j);
                                        stuffs.set(j,stuffs.get(j-1));
                                        stuffs.set(j-1, startTimeStuff);
                                        exchange = true;
                                    }
                                }
                                if(!exchange) {
                                    break;
                                }
                            }
                            for (Stuff stuff: stuffs){
                                Log.d(TAG, "notifyService stuffs :" + stuff.getStartTime());
                            }
                            if(stuffs.size() != 0) {
                                long triggerAtMillis = simpleDateFormat.parse(stuffs.get(0).getStartTime()).getTime();
                                Log.d(TAG, "onCreate: " + triggerAtMillis);
                                Intent intent1 = new Intent(NotifyService.this, AlarmReceiver.class);
                                intent1.putExtra("USERID", userId);
                                intent1.putExtra("STUFFID", stuffs.get(0).getStuffId());
                                intent1.putExtra("STUFFNAME", stuffs.get(0).getName());
                                intent1.putExtra("STUFFINTRODUCE", stuffs.get(0).getIntroduce());
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
                    public void onStuffsFail(String message) {

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
